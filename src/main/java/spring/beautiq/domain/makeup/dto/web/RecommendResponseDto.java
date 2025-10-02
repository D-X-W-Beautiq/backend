package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendResponseDto {
    private List<RecommendationItem> recommendations = new ArrayList<>();

    public void addRecommendation(String imageName, String imageUrl) {
        recommendations.add(new RecommendationItem(imageName, imageUrl));
    }
}