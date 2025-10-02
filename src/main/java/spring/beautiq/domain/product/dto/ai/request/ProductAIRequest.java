package spring.beautiq.domain.product.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.dto.common.ProductFilter;
import spring.beautiq.domain.skinanalysis.dto.ai.common.SkinAnalysisAI;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAIRequest {

    @JsonProperty("predictions")
    @NotNull
    private SkinAnalysisAI analysis;
    @NotNull
    private Integer topN;
    @NotNull
    private String locale;
    @NotNull
    private ProductFilter filters;

    public static SkinAnalysisAI toAnalysis(SkinAnalysisEntity a) {
        return SkinAnalysisAI.builder()
                .dryness(a.getDryness())
                .foreheadPigmentation(a.getForeheadPigmentation())
                .cheekPigmentation(a.getCheekPigmentation())
                .pore(a.getPore())
                .sagging(a.getSagging())
                .foreheadWrinkle(a.getForeheadWrinkle())
                .glabellusWrinkle(a.getGlabellusWrinkle())
                .perocularWrinkle(a.getPerocularWrinkle())
                .pigmentation(a.getPigmentation())
                .cheekPore(a.getCheekPore())
                .foreheadMoisture(a.getForeheadMoisture())
                .cheekMoisture(a.getCheekMoisture())
                .chinMoisture(a.getChinMoisture())
                .foreheadElasticityR2(a.getForeheadElasticity())
                .cheekElasticityR2(a.getCheekElasticity())
                .chinElasticityR2(a.getChinElasticity())
                .perocularWrinkleRa(a.getPerocularWrinkleRa())
                .build();
    }
}