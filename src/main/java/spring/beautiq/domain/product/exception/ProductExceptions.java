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
    WISHLIST_ALREADY_EXISTS("이미 위시리스트에 추가된 제품입니다.", HttpStatus.CONFLICT),
    INVALID_WISHLIST_ORDER_OPTION("유효하지 않은 정렬 옵션입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SKIN_CATEGORY("유효하지 않은 스킨 카테고리입니다.", HttpStatus.BAD_REQUEST),
    RECOMMENDATION_REQUIRED("추천 제품 정보가 필요합니다.", HttpStatus.BAD_REQUEST),
    AI_SERVER_INVALID_RESPONSE("AI 서버 응답이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PRODUCT_NOT_IN_FILTERED_LIST("AI 서버가 반환한 제품이 필터링된 목록에 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_PRODUCTS_MATCH_FILTERS("필터 조건에 맞는 제품이 없습니다.", HttpStatus.NOT_FOUND),
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
