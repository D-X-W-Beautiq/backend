package spring.beautiq.domain.skinanalysis.dto.common;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Predictions {

    @NotNull
    private Float dryness;

    @NotNull
    private Float foreheadPigmentation;

    @NotNull
    private Float cheekPigmentation;

    @NotNull
    private Float pore;

    @NotNull
    private Float sagging;

    @NotNull
    private Float foreheadWrinkle;

    @NotNull
    private Float glabellusWrinkle;

    @NotNull
    private Float perocularWrinkle;

    @NotNull
    private Float pigmentation;

    @NotNull
    private Float cheekPore;

    @NotNull
    private Float foreheadMoisture;

    @NotNull
    private Float cheekMoisture;

    @NotNull
    private Float chinMoisture;

    @NotNull
    private Float foreheadElasticity;

    @NotNull
    private Float cheekElasticity;

    @NotNull
    private Float chinElasticity;

    @NotNull
    private Float perocularWrinkleRa;

}
