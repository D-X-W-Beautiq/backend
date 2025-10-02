package spring.beautiq.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.product.entity.ProductEntity;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    // ProductEntity에는 user 필드가 없으므로 사용자별 조회 메서드는 Wishlist 전용 엔티티/레포로 분리해야 합니다.
    // 필요 시 WishlistProductRepository를 생성하여 User 기준 조회를 구현하세요.
}
