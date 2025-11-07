package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecommendAiResponseDto {
    private String status;
    private List<RecommendAiItem> results = new ArrayList<>();  // recommendations → results로 변경

    public String getBase64(int index) {
        if (index < 0 || index >= results.size()) {
            throw new IllegalArgumentException("Invalid index: " + index + ", size: " + results.size());
        }
        return this.results.get(index).getStyleImageBase64();
    }

    // 기존 코드 호환성을 위한 getter (서비스 레이어에서 사용 중)
    public List<RecommendAiItem> getRecommendations() {
        return results;
    }
}
