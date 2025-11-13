package spring.beautiq.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "오늘의 뷰티 팁 응답")
public class TodayTipResponse {
    @Schema(description = "오늘의 뷰티 팁 문장", example = "세안 후에는 즉시 보습제를 발라주세요.")
    private final String tip;
}

