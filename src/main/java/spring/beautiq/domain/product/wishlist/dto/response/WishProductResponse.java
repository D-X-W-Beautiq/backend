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
                    "brand": "라운드랩",
                    "productName": "[라운드랩] 1025 독도 토너 200ml",
                    "listPrice": 30000,
                    "salePrice": 25000,
                    "reviewScore": 4.5,
                    "reviewCount": 1234,
                    "description": "독도 해양심층수로 피부를 진정시키는 토너",
                    "imageUrl": "https://example.com/image.jpg"
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
                .brand(productEntity.getBrand())
                .productName(productEntity.getProductName())
                .listPrice(productEntity.getListPrice())
                .salePrice(productEntity.getSalePrice())
                .reviewScore(productEntity.getReviewScore() != null ? productEntity.getReviewScore().floatValue() : 0.0f)
                .reviewCount(productEntity.getReviewCount())
                .description(productEntity.getDescription())
                .imageUrl(productEntity.getImageUrl())
                .build();
        return WishProductResponse.builder()
                .id(entity.getId().toString())
                .userId(entity.getUser().getId().toString())
                .wishProduct(wishProduct)
                .build();
    }
}
