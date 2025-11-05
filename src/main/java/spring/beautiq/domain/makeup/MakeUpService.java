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
     * 메이크업 저장
     */
    @Transactional
    public void saveMakeUp(UUID userId, MakeUpSaveRequestDto saveRequestDto) {

        // s3에서 이미지 영구 저장
        String newImageName = s3Service.saveImage(saveRequestDto.getImageName(), userId);

        MakeUpEntity makeUp = MakeUpEntity.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                .keywords(String.join(",", saveRequestDto.getKeywords()))
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
        makeUpDetailResponseDto.setImageName(makeUpEntity.getImageName().split("/")[3]);
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
     * 메이크업 상세 조회
     */
    public MakeUpDetailResponseDto getMakeUp(UUID userId, UUID makeUpId) {
        MakeUpEntity makeUpEntity = makeUpRepository.findById(makeUpId).orElseThrow(() -> new RuntimeException("MakeUp not found"));

        if(!makeUpEntity.getUser().getId().equals(userId)) { // 조회 시 사용자 권한 검증
            throw new RuntimeException("Unauthorized");
        }

        return MakeUptoMakeUpDetailResponseDto(makeUpEntity);
    }


    /**
     * 메이크업 삭제
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
     */
    public RecommendResponseDto styleRecommend(
            UUID userId,
            MultipartFile sourceImage,
            RecommendRequestDto recommendRequestDto // todo: 사진과 키워드 둘 중 하나만 받을지 미정
    ) throws IOException {

        // sourceImage 유효설 검증
        // todo: 예외 처리
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
                .uri("/styles/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(recommendAiRequestDto)
                .retrieve()
                .bodyToMono(RecommendAiResponseDto.class)
                .timeout(Duration.ofSeconds(30)) // 타임아웃 설정
                .block();

        // todo: 예외 처리
        if(recommendAiResponseDto == null) {
            throw new RuntimeException("AI service error");
        }
        if (recommendAiResponseDto.getRecommendations() == null || recommendAiResponseDto.getRecommendations().size() < 3) {
            throw new IllegalStateException("AI service returned insufficient recommendations");
        }

        // Base64 -> MultipartFile 변환
        int count = recommendAiResponseDto.getRecommendations().size();
        MultipartFile[] styleImages = new MultipartFile[count];
        for (int i = 0; i < count; i++) {
            styleImages[i] = base64ToMultipart(recommendAiResponseDto.getBase64(i));
        }

        // S3에 임시 업로드 후 URL dto에 담기
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        for (MultipartFile styleImage : styleImages) {
            String styleImageName = s3Service.uploadImage(styleImage, userId);
            recommendResponseDto.addRecommendation(styleImageName, s3Service.getPreSignedUrl(styleImageName));
        }

        return recommendResponseDto;
    }

    /**
     * 메이크업 시뮬레이션
     */
    public ImageItem simulateMakeUp(
            UUID userId,
            MultipartFile sourceImage,
            MultipartFile styleImage
            ) throws IOException {

        // sourceImage, styleImage 유효설 검증
        if(sourceImage == null || sourceImage.isEmpty()) {
            throw new IllegalArgumentException("Source image is required");
        }
        if(styleImage == null || styleImage.isEmpty()) {
            throw new IllegalArgumentException("Style image is required");
        }
        String sourceContentType = sourceImage.getContentType();
        if(sourceContentType == null || !sourceContentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid source image file");
        }
        String styleContentType = styleImage.getContentType();
        if(styleContentType == null || !styleContentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid style image file");
        }

        // sourceImage, styleImage Base64 인코딩
        String sourceImageBase64 = multipartToBase64(sourceImage);
        String styleImageBase64 = multipartToBase64(styleImage);

        // 요청 DTO에 이미지, 키워드 담기
        SimulationAiRequestDto simulationAiRequestDto = SimulationAiRequestDto.builder()
                .sourceImageBase64(sourceImageBase64)
                .styleImageBase64(styleImageBase64)
                .build();

        // AI 서버에 JSON 요청
        SimulationAiResponseDto simulationAiResponseDto = webClientBuilder.build().post()
                .uri("/styles/simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(simulationAiRequestDto)
                .retrieve()
                .bodyToMono(SimulationAiResponseDto.class)
                .timeout(Duration.ofSeconds(30)) // 타임아웃 설정
                .block();

        // todo: 예외 처리, 공통 부분 메서드화 & customize와 response dto 통합 고려
        if(simulationAiResponseDto == null || simulationAiResponseDto.getResultImageBase64() == null) {
            throw new RuntimeException("AI service error");
        }

        // Base64 -> MultipartFile 변환
        MultipartFile simulatedImage = base64ToMultipart(simulationAiResponseDto.getResultImageBase64());

        // S3에 임시 업로드 후 URL dto에 담기
        String simulatedImageName = s3Service.uploadImage(simulatedImage, userId);

        return new ImageItem(simulatedImageName, s3Service.getPreSignedUrl(simulatedImageName));
    }

    /**
     * 메이크업 커스터마이즈
     */
    public ImageItem customize(
            UUID userId,
            CustomizeRequestDto customizeRequestDto
    ) throws IOException {
        String currentImageBase64 = s3Service.imageNameToBase64(customizeRequestDto.getImageName());
        if(currentImageBase64 == null) {
            throw new RuntimeException("Image not found in S3");
        }

        // 요청 DTO에 이미지, 편집 정보 담기
        CustomizeAiRequestDto customizeAiRequestDto = new CustomizeAiRequestDto();
        customizeAiRequestDto.setBaseImageBase64(currentImageBase64); // 현재 이미지
        for(CustomizeRequestDto.EditForWeb editForWeb : customizeRequestDto.getEdits()) {
            if(editForWeb.isEdited()) {
                customizeAiRequestDto.addEdit(
                        editForWeb.getRegion(),
                        editForWeb.getIntensity()
                );
            }
        }

        // AI 서버에 JSON 요청
        CustomizeAiResponseDto customizeAiResponseDto = webClientBuilder.build().post()
                .uri("/styles/customize")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customizeAiRequestDto)
                .retrieve()
                .bodyToMono(CustomizeAiResponseDto.class)
                .timeout(Duration.ofSeconds(30)) // 타임아웃 설정
                .block();

        // Base64 -> MultipartFile 변환
        // todo: 예외 처리
        if(customizeAiResponseDto == null || customizeAiResponseDto.getResultImageBase64() == null) {
            throw new RuntimeException("AI service error");
        }
        MultipartFile customizedImage = base64ToMultipart(customizeAiResponseDto.getResultImageBase64());

        // S3에 임시 업로드 후 URL dto에 담기
        String customizedImageName = s3Service.uploadImage(customizedImage, userId);
        return new ImageItem(customizedImageName, s3Service.getPreSignedUrl(customizedImageName));
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
