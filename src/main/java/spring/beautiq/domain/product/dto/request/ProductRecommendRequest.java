package spring.beautiq.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.RecommendProductFilter;

@Getter
@Setter
@AllArgsConstructor
public class ProductRecommendRequest {

    @NotNull
    @Min(1)
    private Integer topN;
    private String locale;
    private RecommendProductFilter filters;
}

