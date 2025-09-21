package spring.beautiq.domain.skinanalysis.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthPoint {

    @NotNull
    private String month;

    @NotNull
    private Integer point;
}
