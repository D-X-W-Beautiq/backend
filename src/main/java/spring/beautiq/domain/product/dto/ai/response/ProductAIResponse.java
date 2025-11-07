package spring.beautiq.domain.product.dto.ai.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        description = "AI 제품 추천 응답 DTO",
        example = """
                {
                  "status": "success",
                  "recommendations": [
                    {
                      "product_id": "550e8400-e29b-41d4-a716-446655440000",
                      "reason": "히알루론산과 판테놀 성분이 풍부하여 건조한 피부에 깊은 수분을 공급합니다."
                    }
                  ]
                }
                """
)
public class ProductAIResponse {

    @NotNull
    @Schema(description = "처리 상태", allowableValues = {"success", "fail"}, example = "success", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @NotNull
    @Schema(description = "추천 제품 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Recommendation> recommendations;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(description = "제품 추천 정보")
    public static class Recommendation {

        @NotNull
        @Schema(description = "제품 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000", type = "string", format = "uuid", requiredMode = Schema.RequiredMode.REQUIRED)
        private String productId;

        @NotNull
        @Schema(description = "LLM이 생성한 개인화 추천 이유", example = "히알루론산과 판테놀 성분이 풍부하여 건조한 피부에 깊은 수분을 공급합니다.", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        private String reason;
    }
}