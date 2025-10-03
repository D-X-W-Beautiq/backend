package spring.beautiq.domain.skinanalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysisEntity, UUID> {
    List<SkinAnalysisEntity> findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            UUID userId, LocalDateTime startInclusive, LocalDateTime endExclusive);

    Optional<SkinAnalysisEntity> findTopByUserIdOrderByCreatedAtDesc(UUID userId);
}
