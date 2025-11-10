package spring.beautiq.domain.auth.oauth2.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import spring.beautiq.domain.auth.oauth2.failurehandler.CustomFailureHandler;
import spring.beautiq.domain.auth.oauth2.service.CustomOAuth2UserService;
import spring.beautiq.domain.auth.oauth2.successhandler.CustomSuccessHandler;
import spring.beautiq.global.jwt.JwtFilter;
import spring.beautiq.global.jwt.JwtUtil;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;
    private final CustomFailureHandler customFailureHandler;

    @Value("${app.oauth2.allowed-origin}")
    private String allowedOrigin;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler,
                          JwtUtil jwtUtil, CustomFailureHandler customFailureHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.customFailureHandler = customFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                //From 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)

                //http basic 인증방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)

                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                //경로별 인가 작업
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/oauth2/authorization/**",
                                "/css/**", "/js/**", "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui.html",
                                "/users/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                //세션 설정(jwt=stateless 상태 필수)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig -> {
                            userInfoEndpointConfig
                                    .userService(customOAuth2UserService);
                            userInfoEndpointConfig
                                    .oidcUserService(oidcUserRequest -> {
                                        OidcUserService delegate = new OidcUserService();
                                        return delegate.loadUser(oidcUserRequest); // keep raw OidcUser
                                    });
                        }))
                        .successHandler(customSuccessHandler)
                        .failureHandler(customFailureHandler)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 인증 실패시 redirect 말고 401 반환
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                )

                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    // 환경 변수에서 프론트엔드 주소 가져오기
                    config.setAllowedOrigins(Collections.singletonList(allowedOrigin));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L);

                    config.setExposedHeaders(java.util.Arrays.asList("Set-Cookie", "Authorization"));
                    return config;

                }));


        return http.build();
    }
}