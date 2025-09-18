package spring.beautiq.domain.skinanalysis.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinStatusHistory {

    @NotNull
    private SkinStatusType skinStatus;

    @NotNull
    private String createdAt;
}
