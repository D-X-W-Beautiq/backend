package spring.beautiq.domain.makeup.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Color {
    private int r; // 0~255
    private int g;
    private int b;
}
