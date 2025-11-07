package spring.beautiq.domain.product.dto.ai.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        description = "AI 제품 추천 요청 DTO",
        example = """
                {
                  "skin_analysis": {
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

    @NotNull
    @Valid
    @Schema(description = "피부 분석 데이터", requiredMode = Schema.RequiredMode.REQUIRED)
    private SkinAnalysisData skinAnalysis;

    @NotNull
    @Schema(description = "기준 미달 카테고리 리스트", example = "[\"moisture\", \"wrinkle\", \"pore\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> recommendedCategories;

    @NotNull
    @Valid
    @Schema(description = "필터링된 제품 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FilteredProduct> filteredProducts;

    @NotNull
    @Schema(description = "언어 설정", example = "ko-KR", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String locale;

    public static ProductAIRequest from(
            SkinAnalysisEntity analysis,
            List<String> recommendedCategories,
            List<ProductEntity> products
    ) {
        return ProductAIRequest.builder()
                .skinAnalysis(SkinAnalysisData.from(analysis))
                .recommendedCategories(recommendedCategories)
                .filteredProducts(products.stream()
                        .map(FilteredProduct::from)
                        .collect(Collectors.toList()))
                .locale("ko-KR")
                .build();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "피부 분석 데이터")
    public static class SkinAnalysisData {

        @NotNull
        @Schema(description = "색소침착 회귀 점수", example = "50", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer pigmentationReg;

        @NotNull
        @Schema(description = "수분 점수", example = "65", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer moistureReg;

        @NotNull
        @Schema(description = "탄력 점수", example = "78.5", type = "number", format = "float", requiredMode = Schema.RequiredMode.REQUIRED)
        private Float elasticityReg;

        @NotNull
        @Schema(description = "주름 회귀 점수", example = "35", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer wrinkleReg;

        @NotNull
        @Schema(description = "모공 회귀 점수", example = "55", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer poreReg;

        public static SkinAnalysisData from(SkinAnalysisEntity entity) {
            return SkinAnalysisData.builder()
                    .pigmentationReg(entity.getPigmentationReg())
                    .moistureReg(entity.getMoistureReg())
                    .elasticityReg(entity.getElasticityReg().floatValue())
                    .wrinkleReg(entity.getWrinkleReg())
                    .poreReg(entity.getPoreReg())
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "필터링된 제품 정보")
    public static class FilteredProduct {

        @NotNull
        @Schema(description = "제품 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", type = "string", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        private String productId;

        @NotNull
        @Schema(description = "제품명", example = "[라운드랩] 1025 독도 토너 200ml", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        private String productName;

        @NotNull
        @Schema(description = "브랜드명", example = "라운드랩", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        private String brand;

        @NotNull
        @Schema(description = "NIA 카테고리", example = "moisture", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        private String category;

        @NotNull
        @Schema(description = "가격", example = "25000", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer price;

        @NotNull
        @Schema(description = "리뷰 평점", example = "4.5", type = "number", format = "float", requiredMode = Schema.RequiredMode.REQUIRED)
        private Float reviewScore;

        @NotNull
        @Schema(description = "리뷰 개수", example = "1234", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer reviewCount;

        @NotNull
        @Schema(description = "주요 성분 리스트", example = "[\"히알루론산\", \"나이아신아마이드\", \"판테놀\"]", type = "array", requiredMode = Schema.RequiredMode.REQUIRED)
        private List<String> ingredients;

        public static FilteredProduct from(ProductEntity e) {
            Integer price = e.getSalePrice() != null ? e.getSalePrice() : e.getListPrice();
            return FilteredProduct.builder()
                    .productId(e.getId().toString())
                    .productName(e.getProductName())
                    .brand(e.getBrand())
                    .category(e.getCategory())
                    .price(price)
                    .reviewScore(e.getReviewScore() == null ? 0f : e.getReviewScore().floatValue())
                    .reviewCount(e.getReviewCount() == null ? 0 : e.getReviewCount())
                    .ingredients(e.getIngredients() == null || e.getIngredients().isBlank() ? List.of() : List.of(e.getIngredients().split(",")))
                    .build();
        }
    }
}