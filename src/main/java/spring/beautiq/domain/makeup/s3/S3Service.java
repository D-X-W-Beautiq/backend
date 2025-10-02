package spring.beautiq.domain.makeup.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    /**
     * S3에 이미지 임시 업로드 하기
     */
    public String uploadImage(MultipartFile image) throws IOException {
        String fileName = "temp/" + UUID.randomUUID(); // 고유한 파일 이름 생성

        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        // S3에 파일 업로드 요청 생성
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata);

        // S3에 파일 업로드
        amazonS3.putObject(putObjectRequest);

        return fileName; // 업로드된 파일 이름 반환
    }

    /**
     * S3에 이미지 영구 저장하기
     */
    public String saveImage(String imageName) {
        // temp/ 이미지 삭제하는지 확인
        // todo: 예외처리
        if (!imageName.startsWith("temp/")) {
            throw new IllegalArgumentException("Only temp images can be saved");
        }

        // image 폴더로 복사
        String newFileName = "images/" + UUID.randomUUID(); // 고유한 파일 이름 생성
        amazonS3.copyObject(bucket, imageName, bucket, newFileName);

        // temp 폴더의 이미지 삭제
        deleteImage(imageName);

        return newFileName; // 영구 저장된 파일 이름 반환
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

    /**
     * S3 파일에 대한 임시 접근 URL 생성하기 (Pre-signed URL)
     */
    public String getPreSignedUrl(String fileName) {
        // 1. URL이 만료될 시간 설정
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5분 후 만료되도록 설정
        expiration.setTime(expTimeMillis);

        // 2. Pre-signed URL 요청 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);

        // 3. URL 생성
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, amazonS3.getRegionName(), fileName);
    }

    public void deleteImage(String imageName) {
        amazonS3.deleteObject(bucket, imageName);
    }
}
