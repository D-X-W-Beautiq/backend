package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    description = "월별 피부 상태 이력 응답 래퍼",
    example = "{\n  \"monthlyHistory\": [\n    {\"skinStatus\":\"GOOD\",\"dayDate\":\"2025-09-06\"},\n    {\"skinStatus\":\"CAUTION\",\"dayDate\":\"2025-09-07\"}\n  ]\n}"
)
public class MonthlySkinStatusResponse {

    @NotNull
    @Valid
    @ArraySchema(
        schema = @Schema(implementation = SkinStatusHistory.class),
        minItems = 0,
        maxItems = 31
    )
    private List<SkinStatusHistory> monthlyHistory;

    public static MonthlySkinStatusResponse of(List<SkinStatusHistory> list) {
        return MonthlySkinStatusResponse.builder().monthlyHistory(list).build();
    }
}
