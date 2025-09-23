package spring.beautiq.domain.product.dto.ai.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.RecommendProduct;
import spring.beautiq.domain.product.dto.common.SkinCategories;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendProductAIResponse {

    private String status;
    private List<SkinCategories> needs;
    private List<RecommendProduct> recommendations;
}

