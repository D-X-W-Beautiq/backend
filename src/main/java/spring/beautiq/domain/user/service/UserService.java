package spring.beautiq.domain.user.service;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
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
        //기본 유저 정보 읽기
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."+ username));

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            userEntity.setUsername(dto.getUsername());
        }

        if(dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
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
        if (!userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다." + username);
        }
        userRepository.deleteByUsername(username);
    }


    public boolean isAccess(String username) {
        return true;
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}

