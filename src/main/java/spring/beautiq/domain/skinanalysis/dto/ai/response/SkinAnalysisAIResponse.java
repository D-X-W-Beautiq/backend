package spring.beautiq.domain.skinanalysis.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@Schema(
        description = "AI 피부 분석 응답 (AI 서버 원본 스펙)",
        example = """
        {
          "status": "success",
          "predictions": {
            "dryness": 70,
            "pigmentation": 45,
            "pore": 60,
            "sagging": 35,
            "wrinkle": 40,
            "pigmentation_reg": 48,
            "moisture_reg": 63,
            "elasticity_reg": 75,
            "wrinkle_reg": 42,
            "pore_reg": 58
          },
          "feedback": "수분 관리가 다소 부족합니다. 보습 강화 루틴을 도입해 보세요."
        }
        """
)
public class SkinAnalysisAIResponse {

    @NotNull
    @Schema(description = "처리 상태", allowableValues = {"success", "fail"}, example = "success", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @NotNull
    @Schema(description = "예측 점수 (모든 점수 0~100 정수)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Predictions predictions;

    @NotNull
    @Schema(description = "AI 분석 피드백", example = "수분 관리가 다소 부족합니다. 보습 강화 루틴을 도입해 보세요.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String feedback;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AI 예측 점수 집합 (0~100)")
    public static class Predictions {
        @NotNull @Min(0) @Max(100) @Schema(description = "건조 점수", example = "70") private Integer dryness;
        @NotNull @Min(0) @Max(100) @Schema(description = "색소침착 점수", example = "45") private Integer pigmentation;
        @NotNull @Min(0) @Max(100) @Schema(description = "모공 점수", example = "60") private Integer pore;
        @NotNull @Min(0) @Max(100) @Schema(description = "처짐 점수", example = "35") private Integer sagging;
        @NotNull @Min(0) @Max(100) @Schema(description = "주름 점수", example = "40") private Integer wrinkle;
        @NotNull @Min(0) @Max(100) @JsonProperty("pigmentation_reg") @Schema(description = "색소침착 회귀 점수", example = "48") private Integer pigmentationReg;
        @NotNull @Min(0) @Max(100) @JsonProperty("moisture_reg") @Schema(description = "수분 점수", example = "63") private Integer moistureReg;
        @NotNull @Min(0) @Max(100) @JsonProperty("elasticity_reg") @Schema(description = "탄력 점수", example = "75") private Integer elasticityReg;
        @NotNull @Min(0) @Max(100) @JsonProperty("wrinkle_reg") @Schema(description = "주름 회귀 점수", example = "42") private Integer wrinkleReg;
        @NotNull @Min(0) @Max(100) @JsonProperty("pore_reg") @Schema(description = "모공 회귀 점수", example = "58") private Integer poreReg;
    }
}
