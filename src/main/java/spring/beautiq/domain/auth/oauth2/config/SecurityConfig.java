package spring.beautiq.domain.auth.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import spring.beautiq.domain.auth.oauth2.service.CustomOAuth2UserService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/beautiq/login", "/oauth2/authorization/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                                .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        //성공 핸들러 없어도 기본 /로 돌아감. 이거 나중에 원하면 핸들러 추가하기
                )
                .logout(Customizer.withDefaults());
        return http.build();
    }

}