package spring.beautiq.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public final class ImageUtil {
    private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

    private ImageUtil() {}

    /**
     * 이미지에서 EXIF 메타데이터(특히 Orientation)를 제거하고
     * 순수 픽셀 기반 이미지(JPEG/PNG)로 재인코딩합니다.
     *
     * 주의:
     * - 회전은 절대 하지 않습니다.
     * - JPEG로 재인코딩할 경우 품질 손실이 있을 수 있습니다.
     *   필요 시 PNG로 강제 변환하도록 변경할 수 있습니다.
     */
    public static byte[] stripExif(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) return imageBytes;

        try (InputStream is = new ByteArrayInputStream(imageBytes)) {

            // 이미지 읽기 (이 단계에서 Orientation 적용 없음)
            BufferedImage img = ImageIO.read(is);
            if (img == null) {
                log.warn("ImageIO failed to read image; returning original bytes");
                return imageBytes;
            }

            // 재인코딩 (EXIF 제거)
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            // JPG로 강제 저장 (PNG로 바꾸고 싶으면 "png"로 변경)
            ImageIO.write(img, "jpg", os);

            return os.toByteArray();

        } catch (Exception e) {
            log.warn("Failed to strip EXIF metadata", e);
            return imageBytes;
        }
    }

    /**
     * MultipartFile 버전 - EXIF 제거 후 byte[] 반환
     */
    public static byte[] stripExif(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new byte[0];
        }
        try {
            return stripExif(file.getBytes());
        } catch (IOException e) {
            log.warn("Failed to strip EXIF from MultipartFile", e);
            return new byte[0];
        }
    }

    /**
     * 이미지 픽셀 기준 세로/가로 판별(height > width).
     * EXIF Orientation은 고려하지 않습니다(이미 제거되었기 때문).
     */
    public static boolean isPortrait(byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            BufferedImage img = ImageIO.read(is);
            if (img == null) return false;
            return img.getHeight() > img.getWidth();
        } catch (Exception e) {
            log.warn("Failed to check portrait orientation", e);
            return false;
        }
    }

    /**
     * MultipartFile 버전 세로 판별
     */
    public static boolean isPortrait(MultipartFile file) {
        try {
            return isPortrait(file.getBytes());
        } catch (Exception e) {
            log.warn("Failed to check portrait for MultipartFile", e);
            return false;
        }
    }
}
