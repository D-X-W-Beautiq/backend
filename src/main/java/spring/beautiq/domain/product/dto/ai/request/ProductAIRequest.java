package spring.beautiq.domain.product.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "AI 제품 추천 요청 DTO",
        example = """
                {
                  "skin_analysis": {
                    "dryness": 75,
                    "pigmentation": 45,
                    "pore": 60,
                    "sagging": 30,
                    "wrinkle": 40,
                    "pigmentation_reg": 50,
                    "moisture_reg": 65,
                    "elasticity_reg": 78.5,
                    "wrinkle_reg": 35,
                    "pore_reg": 55
                  },
                  "recommended_categories": ["moisture", "wrinkle", "pore"],
                  "filtered_products": [
                    {
                      "product_id": "550e8400-e29b-41d4-a716-446655440000",
                      "product_name": "[라운드랩] 1025 독도 토너 200ml",
                      "brand": "라운드랩",
                      "category": "moisture",
                      "price": 25000,
                      "review_score": 4.5,
                      "review_count": 1234,
                      "ingredients": ["히알루론산", "나이아신아마이드", "판테놀"]
                    }
                  ],
                  "locale": "ko-KR"
                }
                """
)
public class ProductAIRequest {

    @JsonProperty("skin_analysis")
    @NotNull
    @Valid
    @Schema(description = "피부 분석 데이터", requiredMode = Schema.RequiredMode.REQUIRED)
    private SkinAnalysisData skinAnalysis;

    @JsonProperty("recommended_categories")
    @NotNull
    @Schema(description = "기준 미달 카테고리 리스트", example = "[\"moisture\", \"wrinkle\", \"pore\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> recommendedCategories;

    @JsonProperty("filtered_products")
    @NotNull
    @Valid
    @Schema(description = "필터링된 제품 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FilteredProduct> filteredProducts;

    @NotNull
    @Schema(description = "언어 설정", example = "ko-KR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locale;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "피부 분석 데이터")
    public static class SkinAnalysisData {
        @NotNull
        @Schema(description = "건조 점수", example = "75")
        private Integer dryness;
        @NotNull
        @Schema(description = "색소침착 점수", example = "45")
        private Integer pigmentation;
        @NotNull
        @Schema(description = "모공 점수", example = "60")
        private Integer pore;
        @NotNull
        @Schema(description = "처짐 점수", example = "30")
        private Integer sagging;
        @NotNull
        @Schema(description = "주름 점수", example = "40")
        private Integer wrinkle;
        @NotNull
        @JsonProperty("pigmentation_reg")
        @Schema(description = "색소침착 회귀 점수", example = "50")
        private Integer pigmentationReg;
        @NotNull
        @JsonProperty("moisture_reg")
        @Schema(description = "수분 점수", example = "65")
        private Integer moistureReg;
        @NotNull
        @JsonProperty("elasticity_reg")
        @Schema(description = "탄력 점수", example = "78.5")
        private Float elasticityReg;
        @NotNull
        @JsonProperty("wrinkle_reg")
        @Schema(description = "주름 회귀 점수", example = "35")
        private Integer wrinkleReg;
        @NotNull
        @JsonProperty("pore_reg")
        @Schema(description = "모공 회귀 점수", example = "55")
        private Integer poreReg;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "필터링된 제품 정보")
    public static class FilteredProduct {
        @NotNull
        @JsonProperty("product_id")
        @Schema(description = "제품 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
        private String productId;
        @NotNull
        @JsonProperty("product_name")
        @Schema(description = "제품명", example = "[라운드랩] 1025 독도 토너 200ml")
        private String productName;
        @NotNull
        @Schema(description = "브랜드명", example = "라운드랩")
        private String brand;
        @NotNull
        @Schema(description = "NIA 카테고리", example = "moisture", allowableValues = {"moisture", "elasticity", "wrinkle", "pigmentation", "pore"})
        private String category;
        @NotNull
        @Schema(description = "가격", example = "25000")
        private Integer price;
        @NotNull
        @JsonProperty("review_score")
        @Schema(description = "리뷰 평점", example = "4.5")
        private Float reviewScore;
        @NotNull
        @JsonProperty("review_count")
        @Schema(description = "리뷰 개수", example = "1234")
        private Integer reviewCount;
        @NotNull
        @Schema(description = "주요 성분 리스트", example = "[\"히알루론산\", \"나이아신아마이드\", \"판테놀\"]")
        private List<String> ingredients;
    }
}