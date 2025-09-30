package spring.beautiq.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import spring.beautiq.domain.product.dto.common.SkinCategories;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.global.base.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ElementCollection
    @CollectionTable(name = "product_needs", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "need", nullable = false)
    List<SkinCategories> needs;

    @Column(nullable = false)
    String productName;

    @Column(nullable = false)
    String category;

    @Column(nullable = false)
    Integer price;

    @Column(nullable = false)
    Integer reviewCount;

    @Column(nullable = false)
    String reason;
}
