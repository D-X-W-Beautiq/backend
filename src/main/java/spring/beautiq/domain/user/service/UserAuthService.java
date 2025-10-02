package spring.beautiq.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

/**
 * OAuth2 / OIDC 로그인 시 사용자 생성 또는 변경(동기화) 중복 로직을 캡슐화.
 * 기능상 변화 없이 공통화 & 가독성 향상 목적.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {

    private final UserRepository userRepository;

    /**
     * provider, providerId로 authKey를 구성하여 사용자 upsert.
     * - 신규면 생성 (username 닉네임 초기값 name 값 사용)
     * - 기존이면 name/email 변경 시만 update
     * @param provider    (google, kakao 등)
     * @param providerId  소셜 프로바이더가 주는 고유 subject/id
     * @param name        표시 이름
     * @param email       이메일(없을 수도 있음)
     * @param role        권한(default ROLE_USER)
     * @return 저장/업데이트 된 UserEntity
     */
    public UserEntity upsert(String provider, String providerId, String name, String email, String role) {
        String authKey = provider + "_" + providerId;
        UserEntity user = userRepository.findByAuthKey(authKey);
        if (user == null) {
            user = new UserEntity();
            user.setAuthKey(authKey);
            user.setRole(role);
            user.setName(name);
            user.setEmail(email);
            user.setUsername(name); // 닉네임 초기값
            return userRepository.save(user);
        }
        boolean changed = false;
        if (name != null && !name.equals(user.getName())) { user.setName(name); changed = true; }
        if (email != null && !email.equals(user.getEmail())) { user.setEmail(email); changed = true; }
        if (changed) {
            user = userRepository.save(user);
        }
        return user;
    }
}

