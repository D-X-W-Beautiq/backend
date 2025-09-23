package spring.beautiq.domain.product.dto.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.beautiq.domain.product.entity.ProductEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendReqRes {

    List<SkinCategories> needs;
    RecommendProduct recommendations;

    public static ProductRecommendReqRes from(ProductEntity product) {
        return ProductRecommendReqRes.builder()
                .needs(product.getNeeds())
                .recommendations(
                        RecommendProduct.builder()
                                .productName(product.getProductName())
                                .category(product.getCategory())
                                .price(product.getPrice())
                                .reviewCount(product.getReviewCount())
                                .reason(product.getReason())
                                .build()
                )
                .build();
    }
}
