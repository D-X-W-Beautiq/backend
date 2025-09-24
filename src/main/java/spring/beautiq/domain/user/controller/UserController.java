package spring.beautiq.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.global.security.guard.MemberGuard;

import java.util.UUID;

@RestController
@MemberGuard
public class UserController {

    @GetMapping("/success")
    public String success() {
        return "OAuth2 Login Success! JWT 쿠키가 발급되었습니다 🚀";
    }
}