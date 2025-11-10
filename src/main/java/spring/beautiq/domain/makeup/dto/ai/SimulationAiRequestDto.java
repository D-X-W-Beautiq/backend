package spring.beautiq.domain.makeup.dto.ai;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "AI 메이크업 시뮬레이션 요청 DTO")
public class SimulationAiRequestDto {
    @NotNull
    @Schema(description = "사용자 얼굴 이미지 (Base64)", required = true)
    private String sourceImageBase64;

    @NotNull
    @Schema(description = "참조 메이크업 스타일 이미지 (Base64)", required = true)
    private String styleImageBase64;
}
