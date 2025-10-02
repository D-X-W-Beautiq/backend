package spring.beautiq.domain.makeup.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationAiRequestDto {
    private String sourceImageBase64;
    private String styleImageBase64;
    private String[] keywords;
}
