package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.MonthPoint;
import spring.beautiq.domain.skinanalysis.dto.common.SkinYearFeedbackType;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "연간 월별 피부 평균 점수 및 연간 피드백 응답",
    example = """
        {
          "yearlyHistory": [
            { "monthDate": "2025-01", "point": 78 },
            { "monthDate": "2025-02", "point": 80 },
            { "monthDate": "2025-03", "point": 82 }
          ],
          "feedback": "피부 종합 점수가 꾸준히 상승하고 있습니다. 좋은 관리 습관을 유지하세요!",
          "feedbackType": "UPWARD"
        }
        """
)
public class YearlyDaySkinPointsResponse {

    @NotNull
    @Valid
    @ArraySchema(
        minItems = 1,
        maxItems = 12,
        schema = @Schema(implementation = MonthPoint.class, description = "해당 연도의 월별 평균 점수 (monthDate=YYYY-MM, point=0~100)")
    )
    private List<MonthPoint> yearlyHistory;

    @NotNull
    @Schema(
        description = "연간 피부 상태 분석 결과 (SkinYearFeedbackType 기반 문자열). 가능한 의미 분류: UPWARD, DOWNWARD, FLAT",
        example = "피부 종합 점수가 꾸준히 상승하고 있습니다. 좋은 관리 습관을 유지하세요!",
        minLength = 5,
        maxLength = 500
    )
    private String feedback;

    @NotNull
    @Schema(
        description = "연간 피부 상태 피드백 유형 (UPWARD, DOWNWARD, FLAT 중 하나)",
        example = "UPWARD",
        type = "string"
    )
    private SkinYearFeedbackType feedbackType;

    public static YearlyDaySkinPointsResponse of(List<MonthPoint> monthlyPoints, SkinYearFeedbackType type) {
        return YearlyDaySkinPointsResponse.builder()
                .yearlyHistory(monthlyPoints)
                .feedback(type.getFeedback())
                .feedbackType(type)
                .build();
    }
}
