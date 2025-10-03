package spring.beautiq.domain.product.wishlist.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import spring.beautiq.domain.product.exception.ProductExceptions;
import spring.beautiq.global.exception.ApiException;

@Getter
@RequiredArgsConstructor
@Schema(description = "위시리스트 정렬 옵션")
public enum WishlistOrderOption {

    @Schema(description = "평점 높은 순")
    RATE("rate"),

    @Schema(description = "인기순 (리뷰 많은 순)")
    POPULAR("popular"),

    @Schema(description = "최신순 (등록일 기준)")
    NEWEST("newest");

    private final String param;

    public static WishlistOrderOption fromParam(String param) {
        for (WishlistOrderOption option : values()) {
            if (option.param.equals(param)) {
                return option;
            }
        }
        throw new ApiException(ProductExceptions.INVALID_WISHLIST_ORDER_OPTION);
    }
}
