package spring.beautiq.domain.product.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.product.wishlist.dto.common.WishProduct;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "위시리스트 제품 응답 DTO",
        example = """
                {
                  "wishlistProductId": "550e8400-e29b-41d4-a716-446655440000",
                  "userId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
                  "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
                  "wishlistProduct": {
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
    @Schema(description = "위시리스트 제품 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String wishlistProductId;

    @NotNull
    @Schema(description = "사용자 ID", example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8")
    private String userId;

    @NotNull
    @Schema(description = "제품 고유 ID", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
    private String productId;

    @NotNull
    @Valid
    @Schema(description = "위시리스트 제품 정보")
    private WishProduct wishlistProduct;
}
