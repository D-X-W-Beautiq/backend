package spring.beautiq.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

import java.util.UUID;

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
    public void updateOneUserById(UserRequest dto, UUID userId) {
        validateRequest(dto);

        UserEntity userEntity = findUserById(userId);

        updateUsername(userEntity, dto.getUsername());
        updateEmail(userEntity, dto.getEmail());

        userRepository.save(userEntity);
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

        userEntity.setEmail(trimmedEmail);
    }
}
