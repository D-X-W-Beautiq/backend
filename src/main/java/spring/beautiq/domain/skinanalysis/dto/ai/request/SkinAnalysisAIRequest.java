package spring.beautiq.domain.skinanalysis.dto.ai.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        description = "AI 피부 분석 요청 DTO (이미지를 Base64 문자열로 전달)",
        example = """
        {
          "image_base64": "iVBORw0KGgoAAAANSUhEUgAA... (생략) ..."
        }
        """
)
public class SkinAnalysisAIRequest {

    @NotNull
    @Schema(
            description = "분석 대상 원본 이미지(Base64 인코딩). 'data:*;base64,' prefix 는 제거한 순수 Base64 본문을 권장.",
            example = "iVBORw0KGgoAAAANSUhEUgAA...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 50,
            maxLength = 800000 // 약 ~600KB (Base64) - 정책에 맞게 조정 가능
    )
    private String imageBase64;
}