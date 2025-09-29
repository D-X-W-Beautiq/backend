package spring.beautiq.domain.user.service;


import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.domain.user.dto.UserDTO;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional
    public void cretaeOneUser(UserRequest dto) {
        String username = dto.getUsername();
        String profileImage = dto.getProfileImage();

        //동일한 이름 있는지 확인
        if (userRepository.existsByUsername(username)) {
            return;
        }

        //유저에 대한 entity 생성
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(UUID.randomUUID().toString());
        userEntity.setEmail(username);

        userRepository.save(userEntity);
    }


    @Transactional(readOnly = true)
    public UserResponse readOneUser(String username) {
        UserEntity entity = userRepository.findByUsername(username).orElseThrow();
        UserResponse dto = new UserResponse();
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        return dto;
    }



    //유저 로그인
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .build();
    }

    @Transactional
    public void updateOneUser(UserRequest dto, String username) {
        //기본 유저 정보 읽기
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            userEntity.setPassword(dto.getPassword());
        }

        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            userEntity.setUsername(dto.getUsername());
        }

        userRepository.save(userEntity);
        }


    @Transactional
    public void updateProfileImage(String username, String imageurl) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        userEntity.setProfileImage(imageurl);
        userRepository.save(userEntity);
    }



    @Transactional
    public void deleteOneUser(String username) {
        userRepository.deleteByUsername(username);
    }


    public boolean isAccess(String username) {
        return true;
    }
}