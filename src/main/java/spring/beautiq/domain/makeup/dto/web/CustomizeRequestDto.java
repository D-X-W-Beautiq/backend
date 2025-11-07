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
        @NotBlank
        @Schema(description = "편집 영역 (필수): skin | lip | eyelid | blush", example = "lip", requiredMode = Schema.RequiredMode.REQUIRED)
        private String region;

        @NotNull
        @Min(0)
        @Max(100)
        @Schema(description = "편집 강도 (0~100). 기본값은 50입니다 — 50보다 크면 메이크업이 더 진하게 적용되고, 50보다 작으면 더 연하게 적용됩니다.", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer intensity;
    }
}
