package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "스타일 추천 응답 DTO")
public class RecommendResponseDto {
    @Schema(description = "추천 스타일 목록 (3개)")
    private List<RecommendItem> recommendations = new ArrayList<>();

    public void addRecommendation(String imageName, String imageUrl) {
        recommendations.add(new RecommendItem(imageName, imageUrl));
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "추천 스타일 항목")
    public static class RecommendItem {
        @Schema(description = "이미지 이름", example = "temp/7f000001-9a61-1a59-819a-61ba765e0603/85708e33...")
        private String imageName;

        @Schema(description = "스타일 이미지 s3 url", example = "https://beautiq-s3.s3.amazonaws.com/temp/7f000001-9a61-1a59-819a-61ba765e0603/85708e33-838c-411d-89f0-df271c10ae5e.png?X-Am...")
        private String imageUrl;
    }
}