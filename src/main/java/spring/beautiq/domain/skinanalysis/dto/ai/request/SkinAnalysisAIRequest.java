package spring.beautiq.domain.skinanalysis.dto.ai.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisAIRequest {

    @NotNull
    private String source_image_base64;
}

