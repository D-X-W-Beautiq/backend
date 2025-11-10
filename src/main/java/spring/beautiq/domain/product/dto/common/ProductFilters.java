package spring.beautiq.domain.product.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * 제품 필터 옵션
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "제품 필터 옵션")
public class ProductFilters {

    @Schema(description = "가격 범위 필터")
    private PriceFilter price;

    @Schema(description = "리뷰 평점 필터")
    private ReviewScoreFilter reviewScore;

    @Schema(description = "리뷰 개수 필터")
    private ReviewCountFilter reviewCount;

    /**
     * 가격 필터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "가격 필터")
    public static class PriceFilter {

        @Min(0)
        @Schema(description = "최소 가격 (원)", example = "10000", type = "integer", format = "int32", minimum = "0")
        private Integer min;

        @Min(0)
        @Schema(description = "최대 가격 (원)", example = "50000", type = "integer", format = "int32", minimum = "0")
        private Integer max;
    }

    /**
     * 리뷰 평점 필터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "리뷰 평점 필터")
    public static class ReviewScoreFilter {

        @DecimalMin("0.0")
        @DecimalMax("5.0")
        @Schema(description = "최소 리뷰 평점 (0.0-5.0)", example = "4.0", type = "number", format = "double", minimum = "0.0", maximum = "5.0")
        private Double min;
    }

    /**
     * 리뷰 개수 필터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "리뷰 개수 필터")
    public static class ReviewCountFilter {

        @Min(0)
        @Schema(description = "최소 리뷰 개수", example = "50", type = "integer", format = "int32", minimum = "0")
        private Integer min;
    }
}
