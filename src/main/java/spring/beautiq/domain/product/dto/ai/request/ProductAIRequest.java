package spring.beautiq.domain.product.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.product.dto.common.ProductFilter;
import spring.beautiq.domain.skinanalysis.dto.common.SkinAnalysisScores;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAIRequest {

    @JsonProperty("predictions")
    @NotNull
    private SkinAnalysisScores predictions;

    @NotNull
    private Integer topN;

    @NotNull
    private String locale;

    @NotNull
    private ProductFilter filters;

    public static SkinAnalysisScores fromEntity(SkinAnalysisEntity e) {
        return SkinAnalysisScores.builder()
                .dryness(e.getDryness())
                .pigmentation(e.getPigmentation())
                .pore(e.getPore())
                .sagging(e.getSagging())
                .wrinkle(e.getWrinkle())
                .pigmentationReg(e.getPigmentationReg())
                .moistureReg(e.getMoistureReg())
                .elasticityReg(e.getElasticityReg())
                .wrinkleReg(e.getWrinkleReg())
                .poreReg(e.getPoreReg())
                .build();
    }
}