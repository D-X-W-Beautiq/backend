package spring.beautiq.domain.skinanalysis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysis extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    Float dryness;

    Float foreheadPigmentation;

    Float cheekPigmentation;

    Float pore;

    Float sagging;

    Float foreheadWrinkle;

    Float glabellusWrinkle;

    Float perocularWrinkle;

    Float pigmentation;

    Float cheekPore;

    Float foreheadMoisture;

    Float cheekMoisture;

    Float chinMoisture;

    Float foreheadElasticity;

    Float cheekElasticity;

    Float chinElasticity;

    Float perocularWrinkleRa;

    @Lob
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    String feedback;

    Float averageScore;
}
