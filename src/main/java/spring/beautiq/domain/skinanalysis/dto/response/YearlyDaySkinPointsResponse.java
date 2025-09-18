package spring.beautiq.domain.skinanalysis.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.MonthPoint;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YearlyDaySkinPointsResponse {

    @NotNull
    private List<MonthPoint> yearlyHistory;

    @NotNull
    private String feedback;
}
