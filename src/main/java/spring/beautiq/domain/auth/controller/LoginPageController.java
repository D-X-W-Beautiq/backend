package spring.beautiq.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginPageController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}

// 임시 테스트용