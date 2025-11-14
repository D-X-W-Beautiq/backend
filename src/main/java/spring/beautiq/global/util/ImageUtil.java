package spring.beautiq.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
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
}
