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
public class UserController {

    @GetMapping("/success")
    public String success() {
        return "OAuth2 Login Success! JWT 쿠키가 발급되었습니다 🚀";
    }
}