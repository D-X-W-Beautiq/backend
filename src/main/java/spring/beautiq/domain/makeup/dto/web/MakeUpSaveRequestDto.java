package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MakeUpSaveRequestDto {

    @NotBlank(message = "Image Base64 is required")
    @Schema(description = "Base64 인코딩된 이미지 문자열", required = true)
    private String imageBase64;

    @Schema(description = "키워드 배열")
    private String[] keywords;
}
