package spring.beautiq.domain.skinanalysis.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "피부 분석 점수 DTO (0~100 범위의 정수 점수들)")
public class SkinAnalysisScores {

    @NotNull @Min(0) @Max(100) @Schema(description = "건조 점수", example = "75")
    private Integer dryness;

    @NotNull @Min(0) @Max(100) @Schema(description = "색소침착 점수", example = "45")
    private Integer pigmentation;

    @NotNull @Min(0) @Max(100) @Schema(description = "모공 점수", example = "60")
    private Integer pore;

    @NotNull @Min(0) @Max(100) @Schema(description = "처짐 점수", example = "30")
    private Integer sagging;

    @NotNull @Min(0) @Max(100) @Schema(description = "주름 점수", example = "40")
    private Integer wrinkle;

    @NotNull @Min(0) @Max(100) @Schema(description = "색소침착 회귀 점수", example = "50")
    private Integer pigmentationReg;

    @NotNull @Min(0) @Max(100) @Schema(description = "수분 점수", example = "65")
    private Integer moistureReg;

    @NotNull @Min(0) @Max(100) @Schema(description = "탄력 점수", example = "78")
    private Integer elasticityReg;

    @NotNull @Min(0) @Max(100) @Schema(description = "주름 회귀 점수", example = "35")
    private Integer wrinkleReg;

    @NotNull @Min(0) @Max(100) @Schema(description = "모공 회귀 점수", example = "55")
    private Integer poreReg;
}

