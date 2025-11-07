package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "AI 메이크업 시뮬레이션 응답 DTO")
public class SimulationAiResponseDto {
    @Schema(description = "처리 상태 (success|error)")
    private String status;

    @Schema(description = "메이크업 적용된 결과 이미지 (Base64, 성공 시)")
    private String resultImageBase64;

    @Schema(description = "오류 메시지 (실패 시)")
    private String message;
}
