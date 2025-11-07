package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(description = "메이크업 시뮬레이션 요청 DTO")
public class SimulationRequestDto {
    @NotBlank(message = "원본 이미지 Base64가 필수입니다")
    @Schema(description = "사용자 얼굴 원본 이미지 (Base64, 필수)",
            example = "/9j/4AAQSkZJRgABAQAAAQABAAD...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String sourceImageBase64;

    @Schema(description = "참조 스타일 이미지 파일 (선택, styleImageBase64와 둘 중 하나 필수)",
            type = "string",
            format = "binary")
    private MultipartFile styleImage;

    @Schema(description = "참조 스타일 이미지 (Base64, 선택, styleImage와 둘 중 하나 필수)",
            example = "/9j/4AAQSkZJRgABAQAAAQABAAD...")
    private String styleImageBase64;
}

