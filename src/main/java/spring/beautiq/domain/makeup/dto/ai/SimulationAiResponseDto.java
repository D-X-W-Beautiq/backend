package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SimulationAiResponseDto {
    private String status;
    @Getter
    private String resultImageBase64;
}
