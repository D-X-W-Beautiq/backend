package spring.beautiq.domain.product.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinCategory {
    MOISTURE("수분", "moisture"),
    ELASTICITY("탄력", "elasticity"),
    WRINKLE("주름", "wrinkle"),
    PIGMENTATION("색소침착", "pigmentation"),
    PORE("모공", "pore");

    private final String description;
    private final String englishName;
}
