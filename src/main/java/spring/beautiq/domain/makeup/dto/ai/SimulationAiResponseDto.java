package spring.beautiq.domain.makeup.dto.ai;

import lombok.Data;
import lombok.Getter;

@Data
public class SimulationAiResponseDto {
    private String status;
    @Getter
    private String resultImageBase64;
}
