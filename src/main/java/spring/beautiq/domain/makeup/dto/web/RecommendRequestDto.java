package spring.beautiq.domain.makeup.dto.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecommendRequestDto {
    @Size(max = 5, message = "키워드는 최대 5개까지 입력 가능합니다")
    @Schema(description = "스타일 추천 키워드 배열 (선택, 최대 5개)",
            example = "[\"고급스럽게\", \"차분하게\", \"세련된 분위기\", \"우아한 메이크업\", \"톤 다운된 색감\"]",
            nullable = true)
    private String[] keywords;
}
