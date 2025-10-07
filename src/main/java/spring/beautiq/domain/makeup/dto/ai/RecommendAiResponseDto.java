package spring.beautiq.domain.makeup.dto.ai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendAiResponseDto {
    private String status;
    private List<RecommendAiItem> recommendations = new ArrayList<>();

    public String getBase64(int index) {
        if (index < 0 || index >= recommendations.size()) {
            throw new IllegalArgumentException("Invalid index: " + index + ", size: " + recommendations.size());
        }
        return this.recommendations.get(index).getStyleImageBase64();
    }
}
