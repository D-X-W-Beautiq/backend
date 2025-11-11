package spring.beautiq.domain.product.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.product.wishlist.dto.common.WishProduct;
import spring.beautiq.domain.product.wishlist.entity.WishlistProductEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "위시리스트 제품 응답 DTO",
        example = """
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "userId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                  "wishProduct": {
                    "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                    "category": "스킨케어",
                    "overallRank": 1,
                    "pageNumber": 1,
                    "pageRank": 1,
                    "brand": "라운드랩",
                    "productName": "[라운드랩] 1025 독도 토너 200ml",
                    "listPrice": 30000,
                    "salePrice": 25000,
                    "reviewScore": 4.5,
                    "reviewCount": 1234,
                    "ingredients": "정제수, 글리세린, 부틸렌글라이콜, 판테놀, 해조추출물",
                    "description": "독도 해양심층수로 피부를 진정시키는 토너",
                    "tags": "민감성피부, 진정, 보습",
                    "bestOrNew": "BEST",
                    "imageUrl": "https://example.com/image.jpg",
                    "productUrl": "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000183210"
                  }
                }
                """
)
public class WishProductResponse {

    @NotNull
    @Schema(description = "위시리스트 항목 ID", example = "550e8400-e29b-41d4-a716-446655440000", type = "string", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @NotNull
    @Schema(description = "사용자 ID", example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8", type = "string", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @NotNull
    @Valid
    @Schema(description = "위시리스트 제품 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private WishProduct wishProduct;

    public static WishProductResponse from(WishlistProductEntity entity) {
        ProductEntity productEntity = entity.getProduct();
        WishProduct wishProduct = WishProduct.builder()
                .productId(productEntity.getId().toString())
                .category(productEntity.getCategory())
                .overallRank(productEntity.getOverallRank())
                .pageNumber(productEntity.getPageNumber())
                .pageRank(productEntity.getPageRank())
                .brand(productEntity.getBrand())
                .productName(productEntity.getProductName())
                .listPrice(productEntity.getListPrice())
                .salePrice(productEntity.getSalePrice())
                .reviewScore(productEntity.getReviewScore() != null ? productEntity.getReviewScore().floatValue() : 0.0f)
                .reviewCount(productEntity.getReviewCount())
                .ingredients(productEntity.getIngredients())
                .description(productEntity.getDescription())
                .tags(productEntity.getTags())
                .bestOrNew(productEntity.getBestOrNew())
                .imageUrl(productEntity.getImageUrl())
                .productUrl(productEntity.getProductUrl())
                .build();
        return WishProductResponse.builder()
                .id(entity.getId().toString())
                .userId(entity.getUser().getId().toString())
                .wishProduct(wishProduct)
                .build();
    }
}
