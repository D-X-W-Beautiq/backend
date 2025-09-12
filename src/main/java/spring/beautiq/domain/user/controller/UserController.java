package spring.beautiq.domain.user.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;



    /* 내 프로필 조회 */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me (@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

           UUID userId = principal.getAttribute("userId");
        UserEntity u = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(
                UserResponse.builder()
                        .email(u.getEmail())
                        .name(u.getName())
                        .build()
        );
    }

}
