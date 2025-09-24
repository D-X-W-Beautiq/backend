package spring.beautiq.domain.product.dto.ai.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.Product;
import spring.beautiq.domain.product.dto.common.SkinCategories;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAIResponse {

    private String status;
    private List<SkinCategories> needs;
}
