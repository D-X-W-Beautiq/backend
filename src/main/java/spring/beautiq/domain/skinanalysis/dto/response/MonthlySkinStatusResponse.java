package spring.beautiq.domain.skinanalysis.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySkinStatusResponse {

    @NotNull
    private List<SkinStatusHistory> monthlyHistory;
}
