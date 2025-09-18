package spring.beautiq.domain.skinanalysis.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinStatusType {
    GOOD("양호"),
    CAUTION("주의"),
    DANGER("위험");

    private final String description;
}
