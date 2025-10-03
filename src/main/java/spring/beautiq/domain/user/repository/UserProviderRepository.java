package spring.beautiq.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.user.entity.UserProviderEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProviderRepository extends JpaRepository<UserProviderEntity, Long> {

    Optional<UserProviderEntity> findByProviderAndProviderId(String provider, String providerId);

    List<UserProviderEntity> findByUserId(UUID userId);

    boolean existsByProviderAndProviderId(String provider, String providerId);
}

