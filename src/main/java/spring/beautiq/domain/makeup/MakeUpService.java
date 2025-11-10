package spring.beautiq.domain.makeup;

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

    /**
     * 메이크업 저장 - Base64 이미지를 S3에 저장하고 DB에 기록
     */
    @Transactional
    public void saveMakeUp(UUID userId, MakeUpSaveRequestDto saveRequestDto) {
        String imageBase64 = saveRequestDto.getImageBase64();

        if (imageBase64 == null || imageBase64.isBlank()) {
            throw new IllegalArgumentException("Image Base64 is required");
        }

        try {
            // Base64를 MultipartFile로 변환하여 S3에 저장
            MultipartFile imageFile = base64ToMultipart(imageBase64);
            String newImageName = s3Service.uploadImage(imageFile, userId, "");

            MakeUpEntity makeUp = MakeUpEntity.builder()
                    .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                    .keywords(String.join(",", saveRequestDto.getKeywords()))
                    .imageName(newImageName)
                    .build();

            makeUpRepository.save(makeUp);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image to S3", e);
        }
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
            String recommendImageName
            ) throws IOException {

        // 요청 DTO에 이미지 담기
        SimulationAiRequestDto simulationAiRequestDto = SimulationAiRequestDto.builder()
                .sourceImageBase64(s3Service.getSourceImgBase64(userId)) // 저장된 source 이미지 사용
                .styleImageBase64(s3Service.imageNameToBase64(recommendImageName))
                .build();

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
     * Base64 data URI에서 순수 Base64 추출
     */
    private String extractBase64(String base64Data) {
        if (base64Data.startsWith("data:image")) {
            String[] parts = base64Data.split(",");
            return parts.length > 1 ? parts[1] : parts[0];
        }
        return base64Data;
    }

    /**
     * 메이크업 커스터마이즈
     * @return 처리 상태와 결과 이미지(Base64)
     */
    public CustomizeResponseDto customize(
            CustomizeRequestDto customizeRequestDto
    ) {
        // 입력 검증
        if (customizeRequestDto == null || customizeRequestDto.getBaseImageBase64() == null || customizeRequestDto.getBaseImageBase64().isBlank()) {
            return new CustomizeResponseDto("failed", null, "base_image_base64 is required");
        }
        if (customizeRequestDto.getEdits() == null || customizeRequestDto.getEdits().isEmpty()) {
            return new CustomizeResponseDto("failed", null, "edits is required and must contain at least one item");
        }

        String baseImage = extractBase64(customizeRequestDto.getBaseImageBase64());

        // 요청 DTO에 이미지, 편집 정보 담기
        CustomizeAiRequestDto customizeAiRequestDto = new CustomizeAiRequestDto();
        customizeAiRequestDto.setBaseImageBase64(baseImage); // 현재 이미지
        for(CustomizeRequestDto.EditForWeb editForWeb : customizeRequestDto.getEdits()) {
            if(editForWeb.isEdited()) {
                // intensity 범위는 DTO에서 검증되지만 방어적으로 보정
                int intensity = Math.max(0, Math.min(100, editForWeb.getIntensity()));
                customizeAiRequestDto.addEdit(
                        editForWeb.getRegion(),
                        intensity
                );
            }
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
            return new CustomizeResponseDto("failed", null, "AI service error: no response");
        }

        String status = customizeAiResponseDto.getStatus();
        String resultBase64 = customizeAiResponseDto.getResultImageBase64();
        String message = customizeAiResponseDto.getMessage();

        if (!"success".equalsIgnoreCase(status)) {
            return new CustomizeResponseDto(status == null ? "failed" : status, null, message == null ? "AI processing failed" : message);
        }

        if (resultBase64 == null || resultBase64.isBlank()) {
            return new CustomizeResponseDto("failed", null, "AI returned empty result image");
        }

        return new CustomizeResponseDto("success", resultBase64, null);
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
