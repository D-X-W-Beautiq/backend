package spring.beautiq.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.product.entity.ProductEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {
    Optional<ProductEntity> findByIdAndUser_Id(UUID id, UUID userId);
    Page<ProductEntity> findAllByUser_Id(UUID userId, Pageable pageable);
}
