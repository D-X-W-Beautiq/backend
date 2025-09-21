package spring.beautiq.domain.skinanalysis.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("source_image_base64")
    private String sourceImageBase64;
}