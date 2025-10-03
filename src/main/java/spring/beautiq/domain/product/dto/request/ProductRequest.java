package spring.beautiq.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import spring.beautiq.domain.product.dto.common.ProductFilters;
import spring.beautiq.domain.product.dto.common.ProductSort;
import spring.beautiq.domain.product.dto.common.SkinCategory;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = "개인화 제품 추천 요청",
        example = """
                {
                  "topN": 10,
                  "categories": ["MOISTURE", "WRINKLE", "PORE"],
                  "filters": {
                    "price": {
                      "min": 10000,
                      "max": 50000
                    },
                    "reviewScore": {
                      "min": 4.0
                    },
                    "reviewCount": {
                      "min": 50
                    }
                  },
                  "sort": {
                    "by": "reviewScore",
                    "order": "desc"
                  }
                }
                """
)
public class ProductRequest {

    @NotNull
    @Min(1)
    @Schema(
            description = "추천받을 제품의 최대 개수",
            example = "10",
            type = "integer",
            format = "int32",
            minimum = "1",
            maximum = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer topN;

    @NotNull
    @Size(min = 1, max = 5)
    @Schema(
            description = "피부 고민 카테고리 목록 (1~5개 선택 가능)",
            example = "[\"MOISTURE\", \"WRINKLE\", \"PORE\"]",
            type = "array",
            minLength = 1,
            maxLength = 5,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<SkinCategory> categories;

    @Valid
    @Schema(
            description = "제품 필터 옵션 (가격, 리뷰 평점, 리뷰 개수)",
            implementation = ProductFilters.class
    )
    private ProductFilters filters;

    @Valid
    @Schema(
            description = "정렬 옵션 (정렬 기준 및 순서)",
            implementation = ProductSort.class
    )
    private ProductSort sort;
}
