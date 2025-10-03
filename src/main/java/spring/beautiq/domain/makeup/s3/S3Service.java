package spring.beautiq.domain.makeup.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

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

    /**
     * S3에 이미지 업로드
     */
    public void uploadImage(MultipartFile image, UUID id) throws IOException {
        ensureEnabled();
        String fileName = id.toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(image.getSize());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, image.getInputStream(), metadata));
    }

    /**
     * Pre-signed URL 생성 (GET)
     */
    public String getPreSignedUrl(String fileName) {
        ensureEnabled();
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 1000L * 60 * 5); // 5분

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
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
}
