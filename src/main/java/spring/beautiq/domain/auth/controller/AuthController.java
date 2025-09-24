package spring.beautiq.domain.auth.controller;


import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.domain.auth.dto.AuthDto;
import spring.beautiq.global.security.annotation.CurrentUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/me")
    public ResponseEntity<AuthDto> me(
            @AuthenticationPrincipal OAuth2User principal,
            @CurrentUserId(required = false) UUID userId
    ) {

        if (principal == null || userId == null) {
            return ResponseEntity.ok(
                    AuthDto.builder()
                            .authenticated(false)
                            .build()
            );
        }

        return ResponseEntity.ok(
                AuthDto.builder()
                        .authenticated(true)
                        .userId(userId)
                        .email(principal.getAttribute("email"))
                        .name(principal.getAttribute("name"))
                        .provider(principal.getAttribute("provider"))
                        .build()
        );
    }
}
