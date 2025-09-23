package spring.beautiq.domain.skinanalysis.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.Predictions;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisResponse {

    @NotNull
    private String id;

    @NotNull
    private String createdAt;

    @NotNull
    private Predictions predictions;

    @NotNull
    private String feedback;

    @NotNull
    private Float averageScore;

    public static SkinAnalysisResponse from(SkinAnalysis skinAnalysis) {
        return SkinAnalysisResponse.builder()
                .id(skinAnalysis.getId().toString())
                .createdAt(skinAnalysis.getCreatedAt().toString())
                .predictions(Predictions.builder()
                        .dryness(skinAnalysis.getDryness())
                        .foreheadPigmentation(skinAnalysis.getForeheadPigmentation())
                        .cheekPigmentation(skinAnalysis.getCheekPigmentation())
                        .pore(skinAnalysis.getPore())
                        .sagging(skinAnalysis.getSagging())
                        .foreheadWrinkle(skinAnalysis.getForeheadWrinkle())
                        .glabellusWrinkle(skinAnalysis.getGlabellusWrinkle())
                        .perocularWrinkle(skinAnalysis.getPerocularWrinkle())
                        .pigmentation(skinAnalysis.getPigmentation())
                        .cheekPore(skinAnalysis.getCheekPore())
                        .foreheadMoisture(skinAnalysis.getForeheadMoisture())
                        .cheekMoisture(skinAnalysis.getCheekMoisture())
                        .chinMoisture(skinAnalysis.getChinMoisture())
                        .foreheadElasticity(skinAnalysis.getForeheadElasticity())
                        .cheekElasticity(skinAnalysis.getCheekElasticity())
                        .chinElasticity(skinAnalysis.getChinElasticity())
                        .perocularWrinkleRa(skinAnalysis.getPerocularWrinkleRa())
                        .build())
                .feedback(skinAnalysis.getFeedback())
                .averageScore(skinAnalysis.getAverageScore())
                .build();
    }
}