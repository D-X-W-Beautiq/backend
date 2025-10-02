package spring.beautiq.domain.makeup.dto.ai;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendAiResponseDto {
    private String status;
    private List<RecommendAiItem> recommendations = new ArrayList<>();

    public String get(int index) {
        return recommendations.get(index).getStyleImageBase64();
    }
}
