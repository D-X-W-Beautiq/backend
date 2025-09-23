package spring.beautiq.domain.skinanalysis.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinYearFeedbackType {
    UPWARD("피부 종합 점수가 꾸준히 상승하고 있습니다. 좋은 관리 습관을 유지하세요!"),
    DOWNWARD("피부 종합 점수가 하락하고 있습니다. 생활 습관이나 관리 방법을 점검해보세요."),
    FLAT("큰 변화 없이 비슷한 수준을 유지하고 있습니다. 꾸준한 관리가 중요합니다.");

    private final String feedback;
}

