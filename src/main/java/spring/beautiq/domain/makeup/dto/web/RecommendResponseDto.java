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

    public void addRecommendation(String styleId, String imageBase64) {
        recommendations.add(new RecommendItem(styleId, imageBase64));
    }

    @Data
    @AllArgsConstructor
    @Schema(description = "추천 스타일 항목")
    public static class RecommendItem {
        @Schema(description = "스타일 ID", example = "아이유_009_face")
        private String styleId;

        @Schema(description = "스타일 이미지 (Base64 인코딩)", example = "/9j/4AAQSkZJRgABAQAAAQABAAD...")
        private String imageBase64;
    }
}