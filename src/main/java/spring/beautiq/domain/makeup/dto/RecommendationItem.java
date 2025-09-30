package spring.beautiq.domain.makeup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationItem {
    private String imageName;
    private String imageUrl;
}
