package spring.beautiq.domain.skinanalysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;

import java.util.List;
import java.util.UUID;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, UUID> {
    List<SkinAnalysis> findAllByUserId(UUID userId);
}
