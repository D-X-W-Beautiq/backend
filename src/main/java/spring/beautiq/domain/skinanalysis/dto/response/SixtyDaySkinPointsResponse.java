package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.DayPoint;
import spring.beautiq.domain.skinanalysis.dto.common.MonthPoint;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "60일간 피부 점수 변화 및 현재 월 평균 점수 응답",
    example = """
        {
          "within60Days": [
            {
              "dayDate": "2025-09-06",
              "point": 85
            },
            {
              "dayDate": "2025-09-07",
              "point": 78
            }
          ],
          "currentMonth": {
            "monthDate": "2025-09",
            "point": 82
          }
        }
        """
)
public class SixtyDaySkinPointsResponse {

    @NotNull
    @Valid
    @ArraySchema(
        minItems = 0,
        maxItems = 60,
        schema = @Schema(implementation = DayPoint.class, description = "최근 60일간의 일별 피부 점수 데이터 (최대 60개)")
    )
    private List<DayPoint> within60Days;

    @NotNull
    @Valid
    @Schema(
        description = "현재 월의 평균 피부 점수",
        implementation = MonthPoint.class,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private MonthPoint currentMonth;
}
