package spring.beautiq.domain.product.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderOption {
    POPULAR("popular"),
    NEWEST("newest");

    private final String order;

    public static OrderOption fromParam(String value) {
        if (value == null) return NEWEST;
        String v = value.trim().toLowerCase();
        for (OrderOption o : values()) {
            if (o.order.equals(v)) return o;
        }
        return NEWEST;
    }
}
