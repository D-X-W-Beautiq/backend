package spring.beautiq.domain.makeup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.makeup.entity.MakeUpEntity;

import java.util.List;
import java.util.UUID;

public interface MakeUpRepository extends JpaRepository<MakeUpEntity, UUID> {
    List<MakeUpEntity> findAllByUserId(UUID userId);
    List<MakeUpEntity> findAllByUserIdAndIsLiked(UUID userId, boolean isLiked);
}
