package spring.beautiq.domain.skinanalysis.dto.ai.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisAI {

    @NotNull
    @JsonProperty("dryness")
    private Float dryness;

    @NotNull
    @JsonProperty("forehead_pigmentation")
    private Float foreheadPigmentation;

    @NotNull
    @JsonProperty("cheek_pigmentation")
    private Float cheekPigmentation;

    @NotNull
    @JsonProperty("pore")
    private Float pore;

    @NotNull
    @JsonProperty("sagging")
    private Float sagging;

    @NotNull
    @JsonProperty("forehead_wrinkle")
    private Float foreheadWrinkle;

    @NotNull
    @JsonProperty("glabellus_wrinkle")
    private Float glabellusWrinkle;

    @NotNull
    @JsonProperty("perocular_wrinkle")
    private Float perocularWrinkle;

    @NotNull
    @JsonProperty("pigmentation")
    private Float pigmentation;

    @NotNull
    @JsonProperty("cheek_pore")
    private Float cheekPore;

    @NotNull
    @JsonProperty("forehead_moisture")
    private Float foreheadMoisture;

    @NotNull
    @JsonProperty("cheek_moisture")
    private Float cheekMoisture;

    @NotNull
    @JsonProperty("chin_moisture")
    private Float chinMoisture;

    @NotNull
    @JsonProperty("forehead_elasticity_R2")
    private Float foreheadElasticityR2;

    @NotNull
    @JsonProperty("cheek_elasticity_R2")
    private Float cheekElasticityR2;

    @NotNull
    @JsonProperty("chin_elasticity_R2")
    private Float chinElasticityR2;

    @NotNull
    @JsonProperty("perocular_wrinkle_Ra")
    private Float perocularWrinkleRa;
}
