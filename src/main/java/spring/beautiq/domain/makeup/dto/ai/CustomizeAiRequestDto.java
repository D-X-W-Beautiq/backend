package spring.beautiq.domain.makeup.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import spring.beautiq.domain.makeup.dto.common.Color;

import java.util.List;

@Data
public class CustomizeAiRequestDto {
    private String baseImageBase64;
    private List<EditForAi> edits;

    @Data
    @AllArgsConstructor
    public static class EditForAi {
        private String region; // "skin" | "eye" | "lip" | "blush"
        private int intensity; // 0~100
        private Color color;
    }

    public void addEdit(String region, int intensity, Color color) {
        this.edits.add(new EditForAi(region, intensity, color));
    }
}