package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomizeAiRequestDto {
    @NotBlank
    private String baseImageBase64;
    @NotNull
    private List<EditForAi> edits = new ArrayList<>();

    public void addEdit(String region, int intensity) {
        this.edits.add(new EditForAi(region, intensity));
    }

    @Data
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class EditForAi {
        @NotBlank
        @Pattern(regexp = "^(skin|lip|eyelid|blush)$", message = "region은 skin, lip, eyelid, blush 중 하나여야 합니다")
        private String region; // "skin" | "lip" | "eyelid" | "blush"

        @Min(value = 0, message = "intensity는 0 이상이어야 합니다")
        @Max(value = 100, message = "intensity는 100 이하여야 합니다")
        private int intensity; // 0~100 (기본 50)
    }
}