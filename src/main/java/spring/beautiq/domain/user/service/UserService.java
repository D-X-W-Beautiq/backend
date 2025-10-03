package spring.beautiq.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserEntity createOneUser(String username, String email, String profileImage) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setEmail(email);
                    if (profileImage != null) {
                        user.setProfileImage(profileImage);
                    }
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    UserEntity user = new UserEntity();
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setProfileImage(profileImage);
                    return userRepository.save(user);
                });
    }


    @Transactional(readOnly = true)
    public UserResponse readOneUser(String username) {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."+ username));
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .profileImage(entity.getProfileImage())
                .build();
    }


    @Transactional
    public void updateOneUser(UserRequest dto, String username) {

        if (dto == null) {
            throw new IllegalArgumentException("요청 본문이 비어있습니다.");
        }

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."+ username));

        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            String newUsername = dto.getUsername().trim();
            if (!newUsername.equals(username) && userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("이미 사용중인 사용자명입니다: " + newUsername);
            }
            userEntity.setUsername(newUsername);

        }

        if(dto.getProfileImage() != null && !dto.getProfileImage().trim().isEmpty()) {
            userEntity.setProfileImage(dto.getProfileImage());
        }

        userRepository.save(userEntity);
        }

    @Transactional
    public void updateProfileImage(String username, String imageurl) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."+ username));
        userEntity.setProfileImage(imageurl);
        userRepository.save(userEntity);
    }

    @Transactional
    public void deleteOneUser(String username) {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다." + username));
        userRepository.deleteByUsername(username);
    }

    public boolean isAccess(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            return false;
        String current = auth.getName();
        boolean isOwner = username != null && username.equals(current);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return isOwner || isAdmin;
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}

