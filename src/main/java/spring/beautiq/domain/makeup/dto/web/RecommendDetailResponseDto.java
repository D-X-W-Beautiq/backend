package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendDetailResponseDto {

    private List<RecommendationItem> recommendations = new ArrayList<>();

    private String[] keywords; // 메이크업 키워드

    public void addRecommendation(String imageName, String imageUrl) {
        recommendations.add(new RecommendationItem(imageName, imageUrl));
    }
}