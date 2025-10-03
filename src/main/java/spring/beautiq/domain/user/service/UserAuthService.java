package spring.beautiq.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.entity.UserProviderEntity;
import spring.beautiq.domain.user.repository.UserProviderRepository;
import spring.beautiq.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * OAuth2 / OIDC 로그인 시 사용자 생성 또는 변경(동기화) 중복 로직을 캡슐화.
 * - 한 계정에 여러 소셜 계정 연동 지원 (email 기반 통합)
 * - provider + providerId 조합으로 UserProvider 관리
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {

    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;

    /**
     * provider, providerId로 사용자 upsert.
     *
     * 로직:
     * 1. provider + providerId로 기존 UserProvider 조회
     * 2. 있으면 해당 User 반환 (정보 업데이트)
     * 3. 없으면:
     *    - email이 있고, 해당 email의 User가 있으면 → 기존 User에 새 Provider 추가 (계정 통합)
     *    - 없으면 → 새 User + Provider 생성
     *
     * @param provider    (google, kakao 등)
     * @param providerId  소셜 프로바이더가 주는 고유 subject/id
     * @param name        표시 이름
     * @param email       이메일(없을 수도 있음)
     * @param role        권한(default ROLE_USER)
     * @return 저장/업데이트 된 UserEntity
     */
    public UserEntity upsert(String provider, String providerId, String name, String email, String role) {
        // 1. 기존 Provider 조회
        Optional<UserProviderEntity> existingProvider = userProviderRepository.findByProviderAndProviderId(provider, providerId);

        if (existingProvider.isPresent()) {
            // 기존 소셜 계정으로 로그인 → User 정보 업데이트
            UserEntity user = existingProvider.get().getUser();
            updateUserInfo(user, name, email);
            user.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(user);
        }

        // 2. 새 소셜 계정 로그인 → email 기반 계정 통합 시도
        UserEntity user = null;
        if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email).orElse(null);
        }

        if (user == null) {
            // 3. 신규 User 생성
            user = new UserEntity();
            user.setRole(role != null ? role : "ROLE_USER");
            user.setName(name);
            user.setEmail(email);
            user.setUsername(name != null ? name : "USER"); // 닉네임 초기값
            user.setLastLoginAt(LocalDateTime.now());
            user = userRepository.save(user);
        } else {
            // 4. 기존 User에 새 Provider 연동
            updateUserInfo(user, name, email);
            user.setLastLoginAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        // 5. 새 Provider 추가
        UserProviderEntity newProvider = new UserProviderEntity();
        newProvider.setUser(user);
        newProvider.setProvider(provider);
        newProvider.setProviderId(providerId);
        userProviderRepository.save(newProvider);

        return user;
    }

    private void updateUserInfo(UserEntity user, String name, String email) {
        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
        }
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
        }
    }
}
