package spring.beautiq.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.ProductFilter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull
    @Min(1)
    private Integer topN;
    @NotNull
    private String locale;
    @NotNull
    private ProductFilter filters;
}

