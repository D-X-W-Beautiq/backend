package spring.beautiq.domain.user.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import spring.beautiq.domain.product.wishlist.entity.WishlistProductEntity;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;
import spring.beautiq.global.base.BaseEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(unique = true)
    private String providerId; // google_123456, kakao_789012

    private String role;

    @Column(length = 2048)
    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<SkinAnalysisEntity> skinAnalyses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WishlistProductEntity> wishlistProducts;
}
