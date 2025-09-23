package spring.beautiq.domain.skinanalysis.dto.ai.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.ai.common.SkinAnalysisAI;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisAIResponse {

    @NotNull
    private String status;

    @NotNull
    private SkinAnalysisAI predictions;

    @NotNull
    private String feedback;

}
