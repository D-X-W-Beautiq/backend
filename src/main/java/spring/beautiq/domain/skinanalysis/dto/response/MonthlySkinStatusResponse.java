package spring.beautiq.domain.skinanalysis.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;

import java.util.List;

@ArraySchema(
    schema = @Schema(implementation = SkinStatusHistory.class),
    minItems = 0,
    maxItems = 31
)
@Schema(
    description = "월별 피부 상태 이력 배열",
    example = "[{\"skinStatus\":\"GOOD\",\"dayDate\":\"2025-09-06\"},{\"skinStatus\":\"CAUTION\",\"dayDate\":\"2025-09-07\"},{\"skinStatus\":\"DANGER\",\"dayDate\":\"2025-09-08\"}]",
    type = "array"
)
public class MonthlySkinStatusResponse {

    public static List<SkinStatusHistory> of(List<SkinStatusHistory> skinStatusHistories) {
        return skinStatusHistories;
    }
}
