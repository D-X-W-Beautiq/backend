package spring.beautiq.domain.product.wishlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.product.wishlist.entity.WishlistProductEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자별 위시리스트 상품 접근 전용 Repository
 */
public interface WishlistProductRepository extends JpaRepository<WishlistProductEntity, UUID> {
    Page<WishlistProductEntity> findAllByUser_Id(UUID userId, Pageable pageable);

    Optional<WishlistProductEntity> findByUser_IdAndProduct_Id(UUID userId, UUID productId);
    boolean existsByUser_IdAndProduct_Id(UUID userId, UUID productId);

    void deleteByUser_IdAndProduct_Id(UUID userId, UUID productId);
}
