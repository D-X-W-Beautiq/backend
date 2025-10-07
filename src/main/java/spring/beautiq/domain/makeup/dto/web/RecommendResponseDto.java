package spring.beautiq.domain.makeup.dto.web;

import lombok.Data;
import spring.beautiq.domain.makeup.dto.common.ImageItem;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecommendResponseDto {
    private List<ImageItem> recommendations = new ArrayList<>();

    public void addRecommendation(String imageName, String imageUrl) {
        recommendations.add(new ImageItem(imageName, imageUrl));
    }
}