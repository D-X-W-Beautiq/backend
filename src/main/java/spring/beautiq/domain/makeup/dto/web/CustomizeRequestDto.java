package spring.beautiq.domain.makeup.dto.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.beautiq.domain.makeup.dto.common.Color;

import java.util.List;

@Data
public class CustomizeRequestDto {
    private String imageName;
    private List<EditForWeb> edits;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EditForWeb {
        private boolean isEdited;
        private String region; // "skin" | "eye" | "lip" | "blush"
        private int intensity; // 0~100
        private Color color;
    }
}
