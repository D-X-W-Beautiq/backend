package spring.beautiq.domain.makeup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.makeup.entity.MakeUpEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MakeUpRepository extends JpaRepository<MakeUpEntity, UUID> {
    List<MakeUpEntity> findAllByUserId(UUID userId);
    Optional<MakeUpEntity> findByUserIdAndImageName(UUID user_id, String imageName);
    Page<MakeUpEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
