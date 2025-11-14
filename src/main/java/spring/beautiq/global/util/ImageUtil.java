package spring.beautiq.global.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageUtil {
    private static final Logger log = LoggerFactory.getLogger(ImageUtil.class);

    private ImageUtil() {}

    /**
     * 바이트 배열을 읽어 비율을 유지한 채 정사각형 캔버스로 패딩하고 PNG로 재인코딩하여 반환합니다.
     * 회전(orientation) 처리는 하지 않습니다.
     */
    public static byte[] normalizeToSquare(byte[] originalBytes) {
        if (originalBytes == null || originalBytes.length == 0) {
            return originalBytes;
        }
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
            if (original == null) {
                log.warn("ImageIO.read returned null for given bytes");
                return originalBytes;
            }

            int width = original.getWidth();
            int height = original.getHeight();
            int maxSide = Math.max(width, height);

            // 캔버스는 ARGB로 생성(투명 배경)
            BufferedImage square = new BufferedImage(maxSide, maxSide, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = square.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.setColor(new Color(255, 255, 255, 0));
                g.fillRect(0, 0, maxSide, maxSide);

                int offsetX = (maxSide - width) / 2;
                int offsetY = (maxSide - height) / 2;
                g.drawImage(original, offsetX, offsetY, null);
            } finally {
                g.dispose();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(square, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.warn("Failed to normalize image to square", e);
            return originalBytes;
        }
    }

    /**
     * MultipartFile을 정규화하여 바이트 배열로 반환합니다.
     */
    public static byte[] normalizeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is null or empty");
        }
        return normalizeToSquare(file.getBytes());
    }

    /**
     * 바이트 배열에서 EXIF 메타데이터가 존재하면 시계방향 90도 회전하여 PNG로 반환합니다.
     * EXIF이 없거나 파싱 실패 시 원본 바이트를 반환합니다.
     */
    public static byte[] rotateIfExifPresent(byte[] originalBytes) throws IOException {
        if (originalBytes == null || originalBytes.length == 0) return originalBytes;

        // EXIF 존재 여부 확인
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(originalBytes));
            ExifIFD0Directory exifDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifDir == null || exifDir.getTags().isEmpty()) {
                return originalBytes; // EXIF 정보 없음
            }
        } catch (Exception e) {
            log.debug("Failed to read EXIF metadata: {}", e.getMessage());
            return originalBytes; // 안전하게 원본 반환
        }

        // 회전 수행
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
        if (original == null) {
            log.warn("ImageIO.read returned null while rotating");
            return originalBytes;
        }

        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage rotated = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            AffineTransform at = new AffineTransform();
            // translate to center, rotate 90deg CW, translate back
            at.translate(h / 2.0, w / 2.0);
            at.rotate(Math.toRadians(90));
            at.translate(-w / 2.0, -h / 2.0);
            g2d.setTransform(at);
            g2d.drawImage(original, 0, 0, null);
        } finally {
            g2d.dispose();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(rotated, "png", baos);
        return baos.toByteArray();
    }

    /**
     * MultipartFile 입력에 대해 EXIF가 있으면 시계방향 90도 회전한 바이트를 반환합니다.
     * EXIF가 없으면 원본 바이트를 반환합니다.
     */
    public static byte[] rotateFileIfExifPresent(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is null or empty");
        }
        return rotateIfExifPresent(file.getBytes());
    }
}
