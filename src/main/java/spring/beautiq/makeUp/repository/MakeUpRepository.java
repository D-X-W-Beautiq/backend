package spring.beautiq.makeUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.makeUp.entity.MakeUp;

public interface MakeUpRepository extends JpaRepository<MakeUp, Long> {
}
