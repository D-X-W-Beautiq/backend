package spring.beautiq.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.beautiq.domain.product.entity.ProductEntity;

import java.util.List;

/**
 * 개인화된 제품 추천 응답 DTO
 * LLM 기반 추천 시스템에서 제품 정보와 추천 이유를 함께 반환
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "개인화된 제품 추천 응답",
        example = """
                {
                  "products": [
                    {
                      "product": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "category": "스킨케어",
                        "overallRank": 1,
                        "pageNumber": 1,
                        "pageRank": 1,
                        "brand": "라운드랩",
                        "productName": "1025 독도 토너",
                        "listPrice": 20000,
                        "salePrice": 15000,
                        "reviewScore": 4.8,
                        "reviewCount": 1234,
                        "ingredients": "정제수, 글리세린, 부틸렌글라이콜, 판테놀, 해조추출물",
                        "description": "민감한 피부를 진정시키는 토너",
                        "tags": "민감성피부, 진정, 보습",
                        "bestOrNew": "BEST",
                        "imageUrl": "https://example.com/image.jpg",
                        "productUrl": "https://example.com/product/1"
                      },
                      "reason": "귀하의 민감성 피부 타입에 적합한 진정 성분이 포함되어 있습니다."
                    }
                  ]
                }
                """
)
public class ProductResponse {

    @NotNull
    @Valid
    @ArraySchema(
            schema = @Schema(implementation = ProductRecommendation.class),
            minItems = 0,
            maxItems = 100
    )
    @Schema(description = "추천 제품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ProductRecommendation> products;

    public static ProductResponse from(List<ProductRecommendation> products) {
        return ProductResponse.builder()
                .products(products)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개인화된 제품 추천 항목")
    public static class ProductRecommendation {

        @NotNull
        @Schema(
                description = "제품 상세 정보",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private ProductInfo product;

        @NotNull
        @Schema(
                description = "LLM이 생성한 개인화 추천 이유",
                example = "귀하의 민감성 피부 타입에 적합한 진정 성분이 포함되어 있습니다.",
                type = "string",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String reason;

        public static ProductRecommendation of(ProductEntity entity, String reason) {
            return ProductRecommendation.builder()
                    .product(ProductInfo.from(entity))
                    .reason(reason)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "제품 상세 정보")
    public static class ProductInfo {

        @NotNull
        @Schema(
                description = "제품 고유 ID",
                example = "550e8400-e29b-41d4-a716-446655440000",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String id;

        @NotNull
        @Schema(
                description = "제품 카테고리",
                example = "스킨케어",
                type = "string",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String category;

        @Schema(
                description = "전체 순위",
                example = "1",
                type = "integer",
                format = "int32"
        )
        private Integer overallRank;

        @Schema(
                description = "페이지 번호",
                example = "1",
                type = "integer",
                format = "int32"
        )
        private Integer pageNumber;

        @Schema(
                description = "페이지 내 순위",
                example = "1",
                type = "integer",
                format = "int32"
        )
        private Integer pageRank;

        @Schema(
                description = "브랜드명",
                example = "라운드랩",
                type = "string"
        )
        private String brand;

        @NotNull
        @Schema(
                description = "제품명",
                example = "1025 독도 토너",
                type = "string",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String productName;

        @Schema(
                description = "정가",
                example = "20000",
                type = "integer",
                format = "int32"
        )
        private Integer listPrice;

        @Schema(
                description = "판매가",
                example = "15000",
                type = "integer",
                format = "int32"
        )
        private Integer salePrice;

        @Schema(
                description = "리뷰 평점",
                example = "4.8",
                type = "number",
                format = "float"
        )
        private Float reviewScore;

        @Schema(
                description = "리뷰 개수",
                example = "1234",
                type = "integer",
                format = "int32"
        )
        private Integer reviewCount;

        @Schema(
                description = "제품 성분",
                example = "정제수, 글리세린, 부틸렌글라이콜, 판테놀, 해조추출물",
                type = "string"
        )
        private String ingredients;

        @Schema(
                description = "제품 설명",
                example = "민감한 피부를 진정시키는 토너",
                type = "string"
        )
        private String description;

        @Schema(
                description = "제품 태그",
                example = "민감성피부, 진정, 보습",
                type = "string"
        )
        private String tags;

        @Schema(
                description = "베스트/신제품 구분",
                example = "BEST",
                type = "string"
        )
        private String bestOrNew;

        @Schema(
                description = "제품 이미지 URL",
                example = "https://example.com/image.jpg",
                type = "string",
                format = "uri"
        )
        private String imageUrl;

        @NotNull
        @Schema(
                description = "제품 상세 페이지 URL",
                example = "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210",
                type = "string",
                format = "uri",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String productUrl;

        public static ProductInfo from(ProductEntity entity) {
            return ProductInfo.builder()
                    .id(entity.getId().toString())
                    .category(entity.getCategory())
                    .overallRank(entity.getOverallRank())
                    .pageNumber(entity.getPageNumber())
                    .pageRank(entity.getPageRank())
                    .brand(entity.getBrand())
                    .productName(entity.getProductName())
                    .listPrice(entity.getListPrice())
                    .salePrice(entity.getSalePrice())
                    .reviewScore(entity.getReviewScore() != null ? entity.getReviewScore().floatValue() : null)
                    .reviewCount(entity.getReviewCount())
                    .ingredients(entity.getIngredients())
                    .description(entity.getDescription())
                    .tags(entity.getTags())
                    .bestOrNew(entity.getBestOrNew())
                    .imageUrl(entity.getImageUrl())
                    .productUrl(entity.getProductUrl())
                    .build();
        }
    }
}
