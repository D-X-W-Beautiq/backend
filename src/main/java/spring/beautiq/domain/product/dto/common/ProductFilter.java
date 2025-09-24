package spring.beautiq.domain.product.dto.common;

import java.util.List;
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

    private Integer budgetMin;
    private Integer budgetMax;
    private List<String> includeCategories;
    private List<String> excludeIngredients;
}

