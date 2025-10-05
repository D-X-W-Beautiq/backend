package spring.beautiq.domain.skinanalysis.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinStatusHistory {

    @NotNull
    @Schema(
            description = "피부 상태 (GOOD, CAUTION, DANGER)",
            example = "GOOD",
            type = "string",
            allowableValues = {"GOOD", "CAUTION", "DANGER"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SkinStatusType skinStatus;

    @NotNull
    @Schema(
            description = "날짜 (YYYY-MM-DD 형식)",
            example = "2023-01-01",
            type = "string",
            format = "date",
            pattern = "^\\d{4}-\\d{2}-\\d{2}$",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String dayDate;
}
