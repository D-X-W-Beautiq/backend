package spring.beautiq.domain.product.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import spring.beautiq.domain.product.entity.ProductEntity;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Table(name = "wishlist_item", indexes = {
        @Index(name = "idx_wishlist_user", columnList = "user_id"),
        @Index(name = "idx_wishlist_product", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class WishlistProductEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;
}

