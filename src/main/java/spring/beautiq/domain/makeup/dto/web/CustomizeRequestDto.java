package spring.beautiq.domain.makeup.dto.web;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "메이크업 커스터마이즈 요청 DTO",
        example = """
                {
                  "base_image_base64": "data:image/png;base64,/9j/4AAQSkZJRgABAQAAAQ...",
                  "edits": [
                    { "region": "lip", "intensity": 60 },
                    { "region": "skin", "intensity": 40 }
                  ]
                }
                """)
public class CustomizeRequestDto {
    @NotBlank(message = "base_image_base64 is required")
    @Schema(description = "원본 얼굴 이미지 (Base64 인코딩)", example = "/9j/4AAQSkZJRgABAQAAAQABAAD...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String baseImageBase64;

    @Valid
    @NotNull(message = "edits는 필수입니다")
    @Schema(description = "편집 항목 배열 (필수)")
    private List<EditForWeb> edits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "편집 항목")
    public static class EditForWeb {
        private boolean isEdited;
        private String region; // "skin" | "eye" | "lip" | "blush"
        private int intensity; // 0~100
    }
}
