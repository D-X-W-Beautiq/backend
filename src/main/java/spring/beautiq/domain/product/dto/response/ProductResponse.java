package spring.beautiq.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.beautiq.domain.product.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

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
                  "product": {
                    "productId": "550e8400-e29b-41d4-a716-446655440000",
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
                  "reason": "귀하의 민감성 피부 타입에 적합한 진정 성분(판테놀, 해조추출물)이 포함되어 있으며, 높은 고객 만족도(4.8점)를 보이는 제품입니다."
                }
                """
)
public class ProductResponse {

    @NotNull
    @Schema(
            description = "제품 상세 정보",
            implementation = ProductInfo.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ProductInfo product;

    @NotNull
    @Schema(
            description = "LLM이 생성한 개인화 추천 이유 - 사용자의 피부 타입, 고민, 선호도를 반영한 맞춤형 설명",
            example = "귀하의 민감성 피부 타입에 적합한 진정 성분(판테놀, 해조추출물)이 포함되어 있으며, 높은 고객 만족도(4.8점)를 보이는 제품입니다.",
            type = "string",
            minLength = 10,
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String reason;

    /**
     * ProductEntity로부터 ProductResponse 생성
     *
     * @param entity ProductEntity
     * @param reason LLM이 생성한 추천 이유
     * @return ProductResponse
     */
    public static ProductResponse from(ProductEntity entity, String reason) {
        return ProductResponse.builder()
                .product(ProductInfo.builder()
                        .id(entity.getId())
                        .category(entity.getCategory())
                        .overallRank(entity.getOverallRank())
                        .pageNumber(entity.getPageNumber())
                        .pageRank(entity.getPageRank())
                        .brand(entity.getBrand())
                        .productName(entity.getProductName())
                        .listPrice(entity.getListPrice())
                        .salePrice(entity.getSalePrice())
                        .reviewScore(entity.getReviewScore())
                        .reviewCount(entity.getReviewCount())
                        .ingredients(entity.getIngredients())
                        .description(entity.getDescription())
                        .tags(entity.getTags())
                        .bestOrNew(entity.getBestOrNew())
                        .imageUrl(entity.getImageUrl())
                        .productUrl(entity.getProductUrl())
                        .build())
                .reason(reason)
                .build();
    }

    /**
     * 여러 ProductEntity로부터 ProductResponse 리스트 생성
     *
     * @param entities ProductEntity 리스트
     * @param reasons  각 제품에 대한 추천 이유 리스트
     * @return ProductResponse 리스트
     */
    public static List<ProductResponse> fromList(
            List<ProductEntity> entities,
            List<String> reasons) {
        if (entities.size() != reasons.size()) {
            throw new IllegalArgumentException("제품 수와 추천 이유 수가 일치하지 않습니다.");
        }

        return java.util.stream.IntStream.range(0, entities.size())
                .mapToObj(i -> from(entities.get(i), reasons.get(i)))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 제품 상세 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "제품 상세 정보")
    public static class ProductInfo {

        @NotNull
        @Schema(
                description = "제품 고유 식별자",
                example = "550e8400-e29b-41d4-a716-446655440000",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private UUID id;

        @NotNull
        @Schema(
                description = "제품 카테고리",
                example = "스킨케어",
                type = "string",
                maxLength = 50,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String category;

        @Schema(
                description = "전체 순위 (올리브영 전체 랭킹)",
                example = "1",
                type = "integer",
                format = "int32",
                minimum = "1"
        )
        private Integer overallRank;

        @Schema(
                description = "페이지 번호",
                example = "1",
                type = "integer",
                format = "int32",
                minimum = "1"
        )
        private Integer pageNumber;

        @Schema(
                description = "페이지 내 순위",
                example = "1",
                type = "integer",
                format = "int32",
                minimum = "1"
        )
        private Integer pageRank;

        @Schema(
                description = "브랜드명",
                example = "라운드랩",
                type = "string",
                maxLength = 100
        )
        private String brand;

        @NotNull
        @Schema(
                description = "제품명",
                example = "1025 독도 토너",
                type = "string",
                maxLength = 200,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String productName;

        @Schema(
                description = "정가 (원)",
                example = "20000",
                type = "integer",
                format = "int32",
                minimum = "0"
        )
        private Integer listPrice;

        @Schema(
                description = "판매가 (원)",
                example = "15000",
                type = "integer",
                format = "int32",
                minimum = "0"
        )
        private Integer salePrice;

        @Schema(
                description = "리뷰 평점 (0.0-5.0)",
                example = "4.8",
                type = "number",
                format = "double",
                minimum = "0.0",
                maximum = "5.0"
        )
        private Double reviewScore;

        @Schema(
                description = "리뷰 개수",
                example = "1234",
                type = "integer",
                format = "int32",
                minimum = "0"
        )
        private Integer reviewCount;

        @Schema(
                description = "제품 성분 목록",
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
                description = "제품 태그 (쉼표로 구분)",
                example = "민감성피부, 진정, 보습",
                type = "string",
                maxLength = 500
        )
        private String tags;

        @Schema(
                description = "베스트/신제품 구분",
                example = "BEST",
                type = "string",
                maxLength = 50,
                allowableValues = {"BEST", "NEW", ""}
        )
        private String bestOrNew;

        @Schema(
                description = "제품 이미지 URL",
                example = "https://example.com/image.jpg",
                type = "string",
                format = "uri",
                maxLength = 500
        )
        private String imageUrl;

        @NotNull
        @Schema(
                description = "제품 상세 페이지 URL",
                example = "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210",
                type = "string",
                format = "uri",
                maxLength = 500,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        private String productUrl;
    }
}
