package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import spring.beautiq.domain.makeup.dto.common.Color;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomizeAiRequestDto {
    private String baseImageBase64;
    private List<EditForAi> edits = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditForAi {
        @NotBlank
        @Pattern(regexp = "^(skin|eye|lip|blush)$", message = "region은 skin, eye, lip, blush 중 하나여야 합니다")
        private String region; // "skin" | "eye" | "lip" | "blush"

        @Min(value = 0, message = "intensity는 0 이상이어야 합니다")
        @Max(value = 100, message = "intensity는 100 이하여야 합니다")
        private int intensity; // 0~100

        @NotNull(message = "color는 필수입니다")
        private Color color;
    }

    public void addEdit(String region, int intensity, Color color) {
        this.edits.add(new EditForAi(region, intensity, color));
    }
}