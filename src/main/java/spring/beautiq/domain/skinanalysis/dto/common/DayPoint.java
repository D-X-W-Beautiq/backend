package spring.beautiq.domain.skinanalysis.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayPoint {

    @NotNull
    @Schema(example = "2023-01-01", description = "날짜 (YYYY-MM-DD 형식)", type = "string", minLength = 10, maxLength = 10, requiredMode = Schema.RequiredMode.REQUIRED)
    private String dayDate;

    @NotNull
    @Schema(example = "85", description = "하루 피부 평균 점수", type = "integer", minimum = "0", maximum = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer point;
}
