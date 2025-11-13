package spring.beautiq.domain.makeup.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class S3Service {
    private static final Logger log = LoggerFactory.getLogger(S3Service.class);

    // S3 빈이 조건부(@ConditionalOnProperty)로 생성되지 않을 수 있으므로 optional 주입
    @Autowired(required = false)
    private AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket:}")
    private String bucket;

    @Value("${app.s3.enabled:false}")
    private boolean s3Enabled;

    private void ensureEnabled() {
        if (!s3Enabled || amazonS3 == null) {
            throw new IllegalStateException("S3 기능이 비활성화되어 있거나 AmazonS3 빈이 없습니다. (app.s3.enabled=true 및 자격/리전 설정 확인)");
        }
    }

    public void uploadSourceImage(MultipartFile sourceImage, UUID userId) throws IOException {

        String fileName = "sourceImg/" + userId;
        uploadImage(sourceImage, userId, fileName);
    }

    public String uploadTempImage(MultipartFile recommendImage, UUID userId) throws IOException {

        String fileName = "temp/" + userId + "/" + UUID.randomUUID();
        return uploadImage(recommendImage, userId, fileName);

    }

    /**
     * S3에 프로필 업로드 하기
     */
    public String uploadImage(MultipartFile image, UUID userId) throws IOException {
        String fileName = "profile/" + userId;
        return uploadImage(image, userId, fileName);
    }

    public String uploadImage(MultipartFile image, UUID userId, String fileName) throws IOException {
        ensureEnabled();
        // 이미지 입력 유효 검증
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be null or empty");
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file type");
        }

        // 이미지 확장자 보존하며 고유한 이름 생성
        String originalImageName = image.getOriginalFilename();
        String extension = (originalImageName != null && originalImageName.contains("."))
                ? originalImageName.substring(originalImageName.lastIndexOf("."))
                : "";

        // temp/{userId}/ 폴더에 저장. 이미지 소유권 기록. png로 통일
        String imageName = fileName + ".png";

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        // S3에 이미지 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, imageName, image.getInputStream(), metadata);

        // S3에 이미지 업로드
        amazonS3.putObject(putObjectRequest);

        return imageName; // 업로드된 이미지 이름 반환
    }

    /**
     * S3에 이미지 영구 저장하기
     */
    public String saveImage(String imageName, UUID userId) {
        // 사용자 소유권 검증
        String expectedPrefix = "temp/" + userId + "/";
        if (!imageName.startsWith(expectedPrefix)) {
            throw new IllegalArgumentException("Access denied: image does not belong to user");
        }

        // image 폴더로 복사
        String extension = imageName.contains(".") ? imageName.substring(imageName.lastIndexOf(".")) : "";
        String newImageName = "images/" + userId+ "/" + UUID.randomUUID() + "/" + LocalDateTime.now().toLocalDate().toString() + extension; // images/userId/uuid/2023-10-05.jpg
        amazonS3.copyObject(bucket, imageName, bucket, newImageName);

        // temp 폴더의 이미지 삭제
        deleteImage(imageName);

        return newImageName; // 영구 저장된 이미지 이름 반환
    }

    /**
     * S3에서 이미지 다운로드 및 Base64 인코딩
     */
    public String imageNameToBase64(String imageName) throws IOException {
        // todo: 예외 처리
        if(imageName == null || imageName.isBlank()) {
            throw new IllegalArgumentException("Image name cannot be null or blank");
        }
        if(!amazonS3.doesObjectExist(bucket, imageName)) {
            throw new IllegalArgumentException("Image does not exist in S3: " + imageName);
        }

        try (S3Object s3Object = amazonS3.getObject(bucket, imageName);
             InputStream inputStream = s3Object.getObjectContent()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    public String downloadImage(String fileName) throws IOException {
        try (S3Object s3Object = amazonS3.getObject(bucket, fileName);
             InputStream inputStream = s3Object.getObjectContent()) {
            byte[] imageBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    /**
     * Pre-signed URL 생성 (GET)
     */
    public String getPreSignedUrl(String fileName) {
        return getPublicUrl(fileName);
//        ensureEnabled();
//        Date expiration = new Date();
//        expiration.setTime(expiration.getTime() + 1000L * 60 * 5); // 5분
//
//        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
//                .withMethod(HttpMethod.GET)
//                .withExpiration(expiration);
//
//        URL url = amazonS3.generatePresignedUrl(request);
//        return url.toString();
    }

    /**
     * 퍼블릭 URL 헬퍼 (버킷 퍼블릭 정책일 때)
     */
    public String getPublicUrl(String fileName) {
        ensureEnabled();
        try {
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
        } catch (Exception e) {
            log.debug("Region 조회 실패, 기본 URL 형식 사용: {}", e.getMessage());
            return String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName);
        }
    }

    public void deleteImage(String imageName) {
        if(!amazonS3.doesObjectExist(bucket, imageName)) {
            log.warn("Image does not exist, but proceeding with delete operation: {}", imageName);
        }
        amazonS3.deleteObject(bucket, imageName);
    }

    public String getSourceImgBase64(UUID userId) {
        String fileName = "sourceImg/" + userId + ".png";
        try {
            return imageNameToBase64(fileName);
        } catch (IOException e) {
            log.error("Failed to get source image from S3 for user {}: {}", userId, e.getMessage());
            throw new IllegalStateException("Source image not found for user: " + userId, e);
        }
    }

}
