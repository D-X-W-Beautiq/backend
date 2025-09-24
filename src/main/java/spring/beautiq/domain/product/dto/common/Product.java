package spring.beautiq.domain.product.dto.common;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    String productName;
    @NotNull
    String category;
    @NotNull
    Integer price;
    @NotNull
    Integer reviewCount;
    @NotNull
    String reason;
}
