package spring.beautiq.domain.product.dto.common;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {

    @NotNull
    private Integer budgetMin;
    @NotNull
    private Integer budgetMax;
    @NotNull
    private List<String> includeCategories;
    @NotNull
    private List<String> excludeIngredients;
}

