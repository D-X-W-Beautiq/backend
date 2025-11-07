package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.beautiq.domain.makeup.dto.common.Color;

import java.util.List;

@Data
@Schema(description = "메이크업 커스터마이즈 요청 DTO")
public class CustomizeRequestDto {
    @NotBlank(message = "이미지 Base64가 필수입니다")
    @Schema(description = "원본 이미지 (Base64 인코딩)",
            example = "/9j/4AAQSkZJRgABAQAAAQABAAD...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String imageBase64;

    @Valid
    @Schema(description = "편집할 메이크업 영역 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<EditForWeb> edits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "메이크업 영역별 편집 정보")
    public static class EditForWeb {
        @Schema(description = "해당 영역이 편집되었는지 여부", example = "true")
        private boolean isEdited;

        @NotBlank
        @Schema(description = "편집 영역 (skin: 피부, eye: 아이, lip: 립, blush: 블러셔)",
                example = "lip",
                allowableValues = {"skin", "eye", "lip", "blush"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        private String region;

        @Min(0)
        @Max(100)
        @Schema(description = "메이크업 강도 (0~100)", example = "75", minimum = "0", maximum = "100")
        private int intensity;

        @NotNull
        @Schema(description = "메이크업 색상 (RGB)", requiredMode = Schema.RequiredMode.REQUIRED)
        private Color color;
    }
}
