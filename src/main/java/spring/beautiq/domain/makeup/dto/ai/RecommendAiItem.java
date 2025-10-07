package spring.beautiq.domain.makeup.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendAiItem {
    private String styleId;
    private String styleImageBase64;
}
