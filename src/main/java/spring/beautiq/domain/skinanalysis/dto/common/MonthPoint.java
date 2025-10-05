package spring.beautiq.domain.skinanalysis.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthPoint {

    @NotNull
    @Schema(
            example = "2023-01",
            description = "월 (YYYY-MM 형식)",
            type = "string",
            minLength = 7,
            maxLength = 7,
            pattern = "^\\d{4}-\\d{2}$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String monthDate;

    @NotNull
    @Schema(
            example = "85",
            description = "한 달 피부 평균 점수",
            type = "integer",
            format = "int32",
            minimum = "0",
            maximum = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer point;
}
