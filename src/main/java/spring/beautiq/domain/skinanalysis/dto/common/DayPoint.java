package spring.beautiq.domain.skinanalysis.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayPoint {

    @NotNull
    private String date;

    @NotNull
    private Integer point;
}
