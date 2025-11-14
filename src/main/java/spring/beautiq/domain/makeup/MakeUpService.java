package spring.beautiq.domain.makeup;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import spring.beautiq.domain.makeup.dto.ai.*;
import spring.beautiq.domain.makeup.dto.common.ImageItem;
import spring.beautiq.domain.makeup.dto.web.*;
import spring.beautiq.domain.makeup.entity.MakeUpEntity;
import spring.beautiq.domain.makeup.repository.MakeUpRepository;
import spring.beautiq.domain.makeup.s3.S3Service;
import spring.beautiq.domain.user.repository.UserRepository;

import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import static java.util.Arrays.asList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MakeUpService {

    private static final Logger log = LoggerFactory.getLogger(MakeUpService.class);
    private final MakeUpRepository makeUpRepository;
    private final UserRepository userRepository;

    private final WebClient.Builder webClientBuilder;

    private final S3Service s3Service;
    private final ObjectMapper objectMapper; // JSON 직렬화용

    // JSON 직렬화 헬퍼 (메서드명 변경: safeJson)
    private String safeJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\":\"serialize failed: " + e.getMessage() + "\"}";
        }
    }

    /**
     * 메이크업 저장 - Base64 이미지를 S3에 저장하고 DB에 기록
     */
    @Transactional
    public void saveMakeUp(UUID userId, MakeUpSaveRequestDto saveRequestDto) {

        String newImageName = s3Service.saveImage(saveRequestDto.getImageName(), userId);

        String[] keywords = saveRequestDto.getKeywords();
        MakeUpEntity makeUp = MakeUpEntity.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                .keywords(keywords == null ? null : String.join(",", keywords))
                .imageName(newImageName)
                .build();

        makeUpRepository.save(makeUp);
    }

    /**
     * 저장한 메이크업 목록 조회
     */
    public MakeUpListResponseDto getMakeUpList(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<MakeUpEntity> makeUpPage = makeUpRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);

        MakeUpListResponseDto makeUpListResponseDto = new MakeUpListResponseDto();
        for (MakeUpEntity makeUpEntity : makeUpPage.getContent()) {
            makeUpListResponseDto.getMakeUps().add(MakeUptoMakeUpDetailResponseDto(makeUpEntity));
        }
        return makeUpListResponseDto;
    }

    private MakeUpDetailResponseDto MakeUptoMakeUpDetailResponseDto(MakeUpEntity makeUpEntity) {
        MakeUpDetailResponseDto makeUpDetailResponseDto = new MakeUpDetailResponseDto();
        makeUpDetailResponseDto.setMakeUpId(makeUpEntity.getId());
        String fullPath = makeUpEntity.getImageName();
        if (fullPath == null || fullPath.isBlank()) {
            throw new IllegalStateException("Image name cannot be null or empty");
        }
        String[] pathSegments = fullPath.split("/");
        if (pathSegments.length < 4) {
            throw new IllegalStateException("Invalid image path format: " + fullPath);
        }
        String fileNameWithExt = pathSegments[3];
        String fileName = fileNameWithExt.contains(".")
                ? fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf("."))
                : fileNameWithExt;
        makeUpDetailResponseDto.setImageName(fileName);
        makeUpDetailResponseDto.setImageUrl(s3Service.getPreSignedUrl(makeUpEntity.getImageName()));
        makeUpDetailResponseDto.setCreatedAt(makeUpEntity.getCreatedAt().toString());

        String keywordsValue = makeUpEntity.getKeywords();
        String[] keywords = (keywordsValue == null || keywordsValue.isBlank())
                ? new String[0]
                : keywordsValue.split(","); // todo: 키워드 구분자 맞춰서 변경
        makeUpDetailResponseDto.setKeywords(asList(keywords));

        return makeUpDetailResponseDto;
    }

    /**
     * 메이크업 상세 조회 (ID 기반)
     */
    public MakeUpDetailResponseDto getMakeUp(UUID userId, UUID makeUpId) {
        MakeUpEntity makeUpEntity = makeUpRepository.findById(makeUpId).orElseThrow(() -> new RuntimeException("MakeUp not found"));

        if(!makeUpEntity.getUser().getId().equals(userId)) { // 조회 시 사용자 권한 검증
            throw new RuntimeException("Unauthorized");
        }

        return MakeUptoMakeUpDetailResponseDto(makeUpEntity);
    }

    /**
     * 메이크업 삭제 (ID 기반)
     */
    @Transactional
    public void deleteMakeUp(UUID userId, UUID makeUpId) {
        MakeUpEntity makeUpEntity = makeUpRepository.findById(makeUpId).orElseThrow(() -> new RuntimeException("MakeUp not found"));
        if(!makeUpEntity.getUser().getId().equals(userId)) { // 삭제 시 사용자 권한 검증
            throw new RuntimeException("Unauthorized");
        }
        // s3에서 이미지 삭제
        try {
            s3Service.deleteImage(makeUpEntity.getImageName());
        } catch (Exception e) {
            log.warn("Failed to delete S3 image: {}", makeUpEntity.getImageName(), e);
        }
        // db에서 메이크업 기록 삭제
        makeUpRepository.delete(makeUpEntity);
    }

    /**
     * 스타일 추천
     * @return Base64 이미지 3개 (S3에 저장하지 않음)
     */
    public RecommendResponseDto styleRecommend(
            UUID userId,
            MultipartFile sourceImage,
            RecommendRequestDto recommendRequestDto
    ) throws IOException {

        // sourceImage 필수 검증
        if (sourceImage == null || sourceImage.isEmpty()) {
            throw new IllegalArgumentException("Source image is required");
        }
        String contentType = sourceImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file");
        }

        // sourceImage Base64 인코딩
        String sourceImageBase64 = multipartToBase64(sourceImage);

        // 요청 DTO에 이미지, 키워드 담기
        RecommendAiRequestDto recommendAiRequestDto = RecommendAiRequestDto.builder()
                .sourceImageBase64(sourceImageBase64)
                .keywords(recommendRequestDto.getKeywords())
                .build();

        // AI 서버에 JSON 요청
        RecommendAiResponseDto recommendAiResponseDto = webClientBuilder.build().post()
                .uri("/style/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(recommendAiRequestDto)
                .retrieve()
                .bodyToMono(RecommendAiResponseDto.class)
                .timeout(Duration.ofMinutes(5))
                .block();

        if(recommendAiResponseDto == null) {
            throw new RuntimeException("AI service error");
        }
        if (recommendAiResponseDto.getRecommendations() == null || recommendAiResponseDto.getRecommendations().size() < 3) {
            throw new IllegalStateException("AI service returned insufficient recommendations");
        }

        // sourceImage 저장하기
        s3Service.uploadSourceImage(sourceImage, userId);

        // Base64 응답 그대로 반환 (S3 저장하지 않음)
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        for (RecommendAiItem item : recommendAiResponseDto.getRecommendations()) {
            MultipartFile recommendImage = base64ToMultipart(item.getStyleImageBase64());// Base64 유효성 검증
            String tempImageName = s3Service.uploadTempImage(recommendImage, userId);
            recommendResponseDto.addRecommendation(tempImageName, s3Service.getPreSignedUrl(tempImageName));
        }

        return recommendResponseDto;
    }

    /**
     * 메이크업 시뮬레이션
     * @return Base64 이미지 (S3에 저장하지 않음)
     */
    public ImageItem simulateMakeUp(
            UUID userId,
            String styleImageName,
            MultipartFile styleImage
            ) throws IOException {

        // styleImageName과 styleImage 중 하나는 필수
        if ((styleImageName == null || styleImageName.isBlank()) && (styleImage == null || styleImage.isEmpty())) {
            throw new IllegalArgumentException("Either styleImageName or styleImage must be provided");
        }

        // 요청 DTO에 원본 이미지 담기
        SimulationAiRequestDto simulationAiRequestDto = SimulationAiRequestDto.builder()
                .sourceImageBase64(s3Service.getSourceImgBase64(userId)) // 저장된 source 이미지 사용
                .build();

        if(styleImage == null || styleImage.isEmpty()) {
            // styleImageName으로 이미지 불러오기
            simulationAiRequestDto.setStyleImageBase64(s3Service.imageNameToBase64(styleImageName));
        } else {
            // 업로드된 styleImage 사용
            simulationAiRequestDto.setStyleImageBase64(multipartToBase64(styleImage));
        }

        // AI 서버에 JSON 요청
        SimulationAiResponseDto simulationAiResponseDto = webClientBuilder.build().post()
                .uri("/makeup/simulate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(simulationAiRequestDto)
                .retrieve()
                .bodyToMono(SimulationAiResponseDto.class)
                .timeout(Duration.ofMinutes(5))
                .block();

        // 예외 처리
        if (simulationAiResponseDto == null) {
            throw new RuntimeException("AI service error: No response");
        }
        if (!"success".equals(simulationAiResponseDto.getStatus())) {
            String errorMessage = simulationAiResponseDto.getMessage() != null
                    ? simulationAiResponseDto.getMessage()
                    : "AI service error";
            throw new RuntimeException("AI service error: " + errorMessage);
        }
        if (simulationAiResponseDto.getResultImageBase64() == null) {
            throw new RuntimeException("AI service error: No result image");
        }

        String SimulatedImageName = s3Service.uploadTempImage(
                base64ToMultipart(simulationAiResponseDto.getResultImageBase64()),
                userId
        );
        return new ImageItem(SimulatedImageName, s3Service.getPreSignedUrl(SimulatedImageName));
    }


    /**
     * 메이크업 커스터마이즈
     * @return 처리 상태와 결과 이미지(Base64)
     */
    public CustomizeResponseDto customize(
            String imageName,
            CustomizeRequestDto customizeRequestDto,
            UUID userId
    ) throws IOException {
        log.info("[CUSTOMIZE] start imageName={} editsCount={} userId={}", imageName, customizeRequestDto == null ? -1 : (customizeRequestDto.getEdits() == null ? -1 : customizeRequestDto.getEdits().size()), userId);
        // 입력 검증
        if (customizeRequestDto == null || imageName == null || imageName.isBlank()) {
            return new CustomizeResponseDto("failed", null, null, "imageName is required");
        }
        if (customizeRequestDto.getEdits() == null || customizeRequestDto.getEdits().isEmpty()) {
            return new CustomizeResponseDto("failed", null, null, "edits is required and must contain at least one item");
        }

        String preImage = s3Service.imageNameToBase64(imageName);

        // 요청 DTO에 이미지, 편집 정보 담기
        CustomizeAiRequestDto customizeAiRequestDto = new CustomizeAiRequestDto();
        customizeAiRequestDto.setBaseImageBase64(preImage);
        for(CustomizeRequestDto.EditForWeb editForWeb : customizeRequestDto.getEdits()) {
            Boolean editedFlag = editForWeb.getIsEdited();
            boolean apply = (editedFlag == null) || Boolean.TRUE.equals(editedFlag); // null 또는 true면 적용
            if(apply) {
                int intensity = Math.max(0, Math.min(100, editForWeb.getIntensity()));
                String region = editForWeb.getRegion();
                if("eye".equalsIgnoreCase(region)) { // AI 패턴에 맞게 매핑
                    region = "eyelid";
                }
                customizeAiRequestDto.addEdit(region, intensity);
            }
        }
        if (log.isDebugEnabled()) {
            String editsJson = safeJson(customizeAiRequestDto.getEdits());
            log.debug("""
================ [AI CUSTOMIZE REQUEST] ================
imageName: {}
baseImageBase64:
  length: {}
  head(120): {}
edits ({} items) JSON:
{}
=======================================================
""", imageName, customizeAiRequestDto.getBaseImageBase64().length(), head(customizeAiRequestDto.getBaseImageBase64(), 120), customizeAiRequestDto.getEdits().size(), editsJson);
        } else {
            log.info("[CUSTOMIZE] baseImage len={} editsApplied={}", preImage.length(), customizeAiRequestDto.getEdits().size());
        }

        // AI 서버에 JSON 요청
        CustomizeAiResponseDto customizeAiResponseDto = webClientBuilder.build().post()
                .uri("/custom/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customizeAiRequestDto)
                .retrieve()
                .bodyToMono(CustomizeAiResponseDto.class)
                .timeout(Duration.ofMinutes(5))
                .block();

        if (customizeAiResponseDto == null) {
            log.warn("[CUSTOMIZE] AI response null imageName={}", imageName);
            return new CustomizeResponseDto("failed", null, null, "AI service error: no response");
        }

        if (log.isDebugEnabled()) { // 이전에 null 반환했으므로 추가 null 체크 불필요
            String result = customizeAiResponseDto.getResultImageBase64();
            int len = result == null ? 0 : result.length();
            log.debug("""
================ [AI CUSTOMIZE RESPONSE] ================
status: {}
message: {}
resultImageBase64:
  length: {}
  head(120): {}
========================================================
""", customizeAiResponseDto.getStatus(), customizeAiResponseDto.getMessage(), len, result == null ? "null" : head(result, 120));
        } else {
            log.info("[CUSTOMIZE] result status={} resultBase64Len={}", customizeAiResponseDto.getStatus(), customizeAiResponseDto.getResultImageBase64() == null ? 0 : customizeAiResponseDto.getResultImageBase64().length());
        }

        String status = customizeAiResponseDto.getStatus();
        String resultBase64 = customizeAiResponseDto.getResultImageBase64();
        String message = customizeAiResponseDto.getMessage();

        if (!"success".equalsIgnoreCase(status)) {
            return new CustomizeResponseDto(status == null ? "failed" : status, null, null, message == null ? "AI processing failed" : message);
        }

        if (resultBase64 == null || resultBase64.isBlank()) {
            return new CustomizeResponseDto("failed", null, null, "AI returned empty result image");
        }

        MultipartFile customizedImage = base64ToMultipart(resultBase64);
        String customizedImageName = s3Service.uploadTempImage(customizedImage, userId);

        return new CustomizeResponseDto("success", customizedImageName, s3Service.getPreSignedUrl(customizedImageName), null);
    }

    private String head(String base64, int limit) {
        if (base64 == null) return "null";
        return base64.length() <= limit ? base64 : base64.substring(0, limit) + "...";
    }


    static String multipartToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    static MultipartFile base64ToMultipart(String base64) {
        if (base64 == null || base64.isBlank()) {
                throw new IllegalArgumentException("Base64 string cannot be null or empty");
            }

        String[] parts = base64.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(imageString);
        } catch (IllegalArgumentException e) { // todo: 예외 처리
            throw new IllegalArgumentException("Invalid Base64 string", e);
        }

        return new Base64DecodedMultipartFile(imageBytes, "image.png", "image/png");
    }

    @SuppressWarnings({"NullableProblems", "null"})
    static class Base64DecodedMultipartFile implements MultipartFile {
        private final byte[] imgContent;
        private final String fileName;
        private final String contentType;

        public Base64DecodedMultipartFile(byte[] imgContent, String fileName, String contentType) {
            this.imgContent = imgContent;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return fileName;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return imgContent == null || imgContent.length == 0;
        }

        @Override
        public long getSize() {
            return imgContent.length;
        }

        @Override
        public byte[] getBytes() {
            return imgContent;
        }

        @Override
        public java.io.InputStream getInputStream() {
            return new java.io.ByteArrayInputStream(imgContent);
        }

        @Override
        public void transferTo(java.io.File dest) throws java.io.IOException, IllegalStateException {
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(dest)) {
                out.write(imgContent);
            }
        }
    }

}
