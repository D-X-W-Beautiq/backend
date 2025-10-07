package spring.beautiq.domain.makeup.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.makeup.entity.MakeUp;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MakeUpRepository extends JpaRepository<MakeUp, UUID> {
    List<MakeUp> findAllByUserId(UUID userId);
    Optional<MakeUp> findByUserIdAndImageName(UUID user_id, String imageName);
    Page<MakeUp> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
