package spring.beautiq.domain.makeup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.makeup.entity.MakeUp;

import java.util.List;
import java.util.UUID;

public interface MakeUpRepository extends JpaRepository<MakeUp, UUID> {
    List<MakeUp> findAllByUserId(UUID userId);
}
