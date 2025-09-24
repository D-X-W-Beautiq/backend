package spring.beautiq.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.User;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.security.annotation.CurrentUserId;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;


    /* 내 프로필 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me (@CurrentUserId UUID userId) {
        User u = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(
                UserResponse.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .name(u.getName())
                        .provider(u.getProvider().name())
                        .build()
        );
    }

}
