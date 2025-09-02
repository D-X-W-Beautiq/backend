package spring.beautiq.domain.auth.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import spring.beautiq.domain.auth.oauth2.service.CustomOAuth2UserService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/oauth2/authorization/**",
                                "/favicon.ico", "/css/**", "/js/**","/getpostman.com/**", "/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                )
                .logout(logout -> {}); // 기본값 사용
        return http.build();
    }
}