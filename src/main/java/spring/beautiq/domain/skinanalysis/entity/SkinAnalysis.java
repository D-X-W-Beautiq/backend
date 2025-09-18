package spring.beautiq.domain.skinanalysis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import spring.beautiq.domain.user.entity.User;
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
    User user;

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

    @jakarta.persistence.Lob
    String feedback;

    Float averageScore;
}
