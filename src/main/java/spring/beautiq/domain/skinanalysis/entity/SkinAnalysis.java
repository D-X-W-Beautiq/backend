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

    Long drynessGrade;

    Long foreheadPigmentation;

    Long cheekPigmentation;

    Long poreGrade;

    Long saggingGrade;

    Long foreheadWrinkle;

    Long glabellusWrinkle;

    Long perocularWrinkle;

    Long pigmentationIndex;

    Long cheekPoreCount;

    Long foreheadMoisture;

    Long cheekMoisture;

    Long chinMoisture;

    Long foreheadElasticity;

    Long cheekElasticity;

    Long chinElasticity;

    Long perocularWrinkleRa;

    String feedback;
}
