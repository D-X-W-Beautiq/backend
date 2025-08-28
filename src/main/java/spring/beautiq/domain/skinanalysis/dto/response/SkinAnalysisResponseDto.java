package spring.beautiq.domain.skinanalysis.dto.response;

import lombok.*;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisResponseDto {

    private String id;
    private Predictions predictions;
    private String feedback;
    private String createdAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Predictions {

        private Long drynessGrade;
        private Long foreheadPigmentation;
        private Long cheekPigmentation;
        private Long poreGrade;
        private Long saggingGrade;
        private Long foreheadWrinkle;
        private Long glabellusWrinkle;
        private Long perocularWrinkle;
        private Long pigmentationIndex;
        private Long cheekPoreCount;
        private Long foreheadMoisture;
        private Long cheekMoisture;
        private Long chinMoisture;
        private Long foreheadElasticity;
        private Long cheekElasticity;
        private Long chinElasticity;
        private Long perocularWrinkleRa;

        public static Predictions from(SkinAnalysis s) {
            return Predictions.builder()
                    .drynessGrade(s.getDrynessGrade())
                    .foreheadPigmentation(s.getForeheadPigmentation())
                    .cheekPigmentation(s.getCheekPigmentation())
                    .poreGrade(s.getPoreGrade())
                    .saggingGrade(s.getSaggingGrade())
                    .foreheadWrinkle(s.getForeheadWrinkle())
                    .glabellusWrinkle(s.getGlabellusWrinkle())
                    .perocularWrinkle(s.getPerocularWrinkle())
                    .pigmentationIndex(s.getPigmentationIndex())
                    .cheekPoreCount(s.getCheekPoreCount())
                    .foreheadMoisture(s.getForeheadMoisture())
                    .cheekMoisture(s.getCheekMoisture())
                    .chinMoisture(s.getChinMoisture())
                    .foreheadElasticity(s.getForeheadElasticity())
                    .cheekElasticity(s.getCheekElasticity())
                    .chinElasticity(s.getChinElasticity())
                    .perocularWrinkleRa(s.getPerocularWrinkleRa())
                    .build();
        }
    }

    public static SkinAnalysisResponseDto from(SkinAnalysis s) {
        return SkinAnalysisResponseDto.builder()
                .id(s.getId().toString())
                .predictions(Predictions.from(s))
                .feedback(s.getFeedback())
                .createdAt(s.getCreatedAt().toString())
                .build();
    }
}