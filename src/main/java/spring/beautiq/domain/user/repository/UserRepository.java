package spring.beautiq.domain.user.repository;


import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.beautiq.domain.user.AuthProvider.AuthProvider;
import spring.beautiq.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByProviderAndProviderId(AuthProvider authProvider, String providerId);
}
