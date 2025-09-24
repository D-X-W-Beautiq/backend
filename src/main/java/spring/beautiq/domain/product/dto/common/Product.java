package spring.beautiq.domain.product.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    String productName;
    String category;
    Integer price;
    Integer reviewCount;
    String reason;
}

