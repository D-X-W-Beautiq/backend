package spring.beautiq.domain.user.service;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse readOneUserById(UUID userId) {
        UserEntity entity = findUserById(userId);
        return mapToResponse(entity);
    }

    @Transactional
    public Map<String, String> updateMyUser(UUID userId, UserRequest request) {
        validateRequest(request);

        UserEntity user = findUserById(userId);

        log.info("=== 사용자 정보 수정 시작 ===");
        log.info("현재 username: {}", user.getUsername());
        log.info("새로운 username: {}", request.getUsername());
        log.info("현재 email: {}", user.getEmail());
        log.info("새로운 email: {}", request.getEmail());

        updateUsername(user, request.getUsername());
        updateEmail(user, request.getEmail());

        userRepository.save(user);
        log.info("=== 사용자 정보 수정 완료 ===");

        Map<String, String> response = new HashMap<>();
        response.put("message", "정보가 성공적으로 수정되었습니다");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("provider", extractProvider(user.getProviderId()));  // ✅ 추가

        return response;
    }

    @Transactional
    public void updateProfileImageById(UUID userId, String imageUrl) {
        UserEntity userEntity = findUserById(userId);
        userEntity.setProfileImage(imageUrl);
        userRepository.save(userEntity);
    }

    @Transactional
    public void deleteUserById(UUID userId) {
        UserEntity userEntity = findUserById(userId);
        userRepository.delete(userEntity);
    }

    // === Private Helper Methods ===

    private UserEntity findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
    }

    private UserResponse mapToResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .profileImage(entity.getProfileImage())
                .provider(extractProvider(entity.getProviderId()))  // ✅ 추가
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private void validateRequest(UserRequest dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청 본문이 비어있습니다.");
        }
    }

    private void updateUsername(UserEntity userEntity, String newUsername) {
        if (newUsername == null || newUsername.trim().isEmpty()) {
            return;
        }

        String trimmedUsername = newUsername.trim();
        if (trimmedUsername.equals(userEntity.getUsername())) {
            return;
        }

        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("이미 사용중인 사용자명입니다: " + trimmedUsername);
        }

        userEntity.setUsername(trimmedUsername);
    }

    private void updateEmail(UserEntity userEntity, String newEmail) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            return;
        }

        String trimmedEmail = newEmail.trim();
        if (trimmedEmail.equals(userEntity.getEmail())) {
            return;
        }

        // 이메일 중복 체크
        if (userRepository.findByEmail(trimmedEmail).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + trimmedEmail);
        }

        userEntity.setEmail(trimmedEmail);
    }

    /**
     * providerId에서 provider 추출
     * @param providerId "kakao_123456" 또는 "google_789012" 형식
     * @return "kakao" 또는 "google", providerId가 null이면 null
     */
    private String extractProvider(String providerId) {
        if (providerId != null && providerId.contains("_")) {
            return providerId.split("_")[0];
        }
        return null;
    }
}