package spring.beautiq.domain.product.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 정렬 옵션
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "정렬 옵션")
public class ProductSort {

    @NotNull
    @Schema(
            description = "정렬 기준",
            example = "reviewScore",
            allowableValues = {"price", "reviewScore", "reviewCount"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String by;

    @NotNull
    @Schema(
            description = "정렬 순서",
            example = "desc",
            allowableValues = {"asc", "desc"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String order;
}

