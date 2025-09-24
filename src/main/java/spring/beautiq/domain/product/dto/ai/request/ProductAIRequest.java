package spring.beautiq.domain.product.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.ProductFilter;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAIRequest {

    @JsonProperty("predictions")
    private SkinAnalysis analysis;
    private Integer topN;
    private String locale;
    private RecommendProductFilter filters;
    @NotNull
}