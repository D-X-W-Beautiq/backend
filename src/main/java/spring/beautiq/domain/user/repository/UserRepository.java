package spring.beautiq.domain.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.user.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByProviderId(String providerId);
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
