package spring.beautiq.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import spring.beautiq.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ProductExceptions implements ErrorCode {
    PRODUCT_NOT_FOUND("제품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WISHLIST_NOT_FOUND("위시리스트를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WISHLIST_FORBIDDEN("위시리스트에 접근할 수 없습니다.", HttpStatus.FORBIDDEN),
    INVALID_SKIN_CATEGORY("유효하지 않은 스킨 카테고리입니다.", HttpStatus.BAD_REQUEST),
    RECOMMENDATION_REQUIRED("추천 제품 정보가 필요합니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
