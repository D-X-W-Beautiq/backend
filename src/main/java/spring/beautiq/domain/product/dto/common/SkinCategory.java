package spring.beautiq.domain.product.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SkinCategory {
    MOISTURE("수분"),
    ELASTICITY("탄력"),
    WRINKLE("주름"),
    PIGMENTATION("색소 침착"),
    PORE("모공");

    private final String description;
}
