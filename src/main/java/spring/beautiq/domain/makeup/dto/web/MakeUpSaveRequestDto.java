package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MakeUpSaveRequestDto {
    @NotBlank(message = "이미지 Base64가 필수입니다")
    @Schema(description = "저장할 이미지 (Base64 인코딩)",
            example = "/9j/4AAQSkZJRgABAQAAAQABAAD...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageBase64;
}
