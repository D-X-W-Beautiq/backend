package spring.beautiq.domain.skinanalysis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "skin_analysis")
public class SkinAnalysisEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_skin_analysis_user"))
    @NotNull
    UserEntity user;

    @Column(name = "pigmentation_reg", nullable = false)
    @NotNull
    @Min(0) @Max(100)
    Integer pigmentationReg;

    @Column(name = "moisture_reg", nullable = false)
    @NotNull
    @Min(0) @Max(100)
    Integer moistureReg;

    @Column(name = "elasticity_reg", nullable = false)
    @NotNull
    @Min(0) @Max(100)
    Integer elasticityReg;

    @Column(name = "wrinkle_reg", nullable = false)
    @NotNull
    @Min(0) @Max(100)
    Integer wrinkleReg;

    @Column(name = "pore_reg", nullable = false)
    @NotNull
    @Min(0) @Max(100)
    Integer poreReg;

    @Lob
    @NotBlank
    @Column(name = "feedback", nullable = false, columnDefinition = "LONGTEXT")
    String feedback;

    @Column(name = "average_score", nullable = false)
    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    Float averageScore;
}
