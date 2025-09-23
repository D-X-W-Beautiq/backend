package spring.beautiq.domain.skinanalysis.dto.response;

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
public class SixtyDaySkinPointsResponse {

    @NotNull
    private List<DayPoint> within60Days;

    @NotNull
    private MonthPoint currentMonth;
}
