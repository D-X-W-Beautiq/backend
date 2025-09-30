package spring.beautiq.domain.makeup.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class RecommendResponseDto {

    private Map<String, String> recommendations = new HashMap<>(); // 이미지 이름:s3 임시 접근 url로 전송

    public void addRecommendation(String imageName, String url) {
        this.recommendations.put(imageName, url);
    }
}
