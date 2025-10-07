package spring.beautiq.domain.makeup.dto.ai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendAiRequestDto {
    private String sourceImageBase64;
    private String[] keywords;
}
