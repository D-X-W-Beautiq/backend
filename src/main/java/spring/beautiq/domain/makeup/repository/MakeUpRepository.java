package spring.beautiq.domain.makeup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.makeup.entity.MakeUp;

public interface MakeUpRepository extends JpaRepository<MakeUp, Long> {
}
