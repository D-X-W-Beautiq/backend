package spring.beautiq.domain.user.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    UserEntity findByAuthKey(String authKey);
}
