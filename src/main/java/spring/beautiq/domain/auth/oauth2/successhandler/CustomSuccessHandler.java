package spring.beautiq.domain.auth.oauth2.successhandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.jwt.JwtUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    @Value("${app.oauth2.redirect:https://localhost:5173/oauth/callback}")
    private String successRedirect;
    @Value("${app.oauth2.cookie.secure:false}")
    private boolean useCookieSecure;
    @Value("${app.oauth2.cookie.domain:}")
    private String cookieDomain;

    public CustomSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        String userId;
        String username;

        if (principal instanceof CustomOAuth2User oAuth2User) {
            userId = oAuth2User.getUserId();
            username = oAuth2User.getUsername();
        } else if (principal instanceof DefaultOAuth2User oAuth2User) {
            // 구글 OIDC의 경우
            Object attrUserId = oAuth2User.getAttributes().get("userId");
            Object attrUsername = oAuth2User.getAttributes().get("username");

            if (attrUserId != null && attrUsername != null) {
                userId = String.valueOf(attrUserId);
                username = String.valueOf(attrUsername);
            } else {
                // sub/email 기반으로 providerId 생성 후 조회/생성
                String sub = String.valueOf(oAuth2User.getAttributes().get("sub"));
                String email = (String) oAuth2User.getAttributes().get("email");
                String providerId = "google_" + sub;

                UserEntity entity = userRepository.findByProviderId(providerId)
                        .orElseGet(() -> userRepository.findByEmail(email)
                                .map(existing -> {
                                    existing.setProviderId(providerId);
                                    return userRepository.save(existing);
                                })
                                .orElseGet(() -> {
                                    UserEntity u = new UserEntity();
                                    u.setProviderId(providerId);
                                    u.setEmail(email);
                                    u.setUsername(generateDefaultUsername(email));
                                    u.setRole("ROLE_USER");
                                    return userRepository.save(u);
                                }));
                userId = entity.getId().toString();
                username = entity.getUsername();
            }
        } else if (principal instanceof OAuth2User oAuth2Generic) {
            // 일반 OAuth2User 케이스
            Object attrUserId = oAuth2Generic.getAttributes().get("userId");
            Object attrUsername = oAuth2Generic.getAttributes().get("username");

            if (attrUserId == null || attrUsername == null) {
                String email = (String) oAuth2Generic.getAttributes().get("email");
                String name = String.valueOf(oAuth2Generic.getAttributes().get("name"));
                String providerId = "oauth_" + name.hashCode();

                UserEntity entity = userRepository.findByProviderId(providerId)
                        .orElseGet(() -> userRepository.findByEmail(email)
                                .map(existing -> {
                                    existing.setProviderId(providerId);
                                    return userRepository.save(existing);
                                })
                                .orElseGet(() -> {
                                    UserEntity u = new UserEntity();
                                    u.setProviderId(providerId);
                                    u.setEmail(email);
                                    u.setUsername(generateDefaultUsername(email));
                                    u.setRole("ROLE_USER");
                                    return userRepository.save(u);
                                }));
                userId = entity.getId().toString();
                username = entity.getUsername();
            } else {
                userId = String.valueOf(attrUserId);
                username = String.valueOf(attrUsername);
            }
        } else {
            throw new IllegalArgumentException("지원하지 않는 principal 타입:: " + principal.getClass().getName());
        }


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // 이메일/프로필/프로바이더 정보 획득 (가능한 경우)
        String email = null;
        String profileImage = null;
        String provider = null;

        if (principal instanceof CustomOAuth2User cu) {
            email = cu.getEmail();
            profileImage = cu.getProfileImage();
            provider = cu.getProvider();
        } else if (principal instanceof DefaultOAuth2User d) {
            Object e = d.getAttributes().get("email");
            if (e != null) email = String.valueOf(e);
            Object pi = d.getAttributes().get("profileImage");
            if (pi == null) pi = d.getAttributes().get("picture");
            if (pi != null) profileImage = String.valueOf(pi);
            // provider는 성공 핸들러에서는 알기 어려워 생략 가능
        }

        String token = jwtUtil.createJwt(userId, username, role, java.util.concurrent.TimeUnit.HOURS.toMillis(60), email, profileImage, provider);

        // JWT를 쿠키로 설정하여 프론트엔드로 전달
        ResponseCookie jwtcookie = createCookie(token);
        response.addHeader("Set-Cookie", jwtcookie.toString());

        response.sendRedirect(successRedirect);
    }

    private String generateDefaultUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }
        return username;
    }

    private ResponseCookie createCookie(String value) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("Authorization", value)
                .maxAge(java.time.Duration.ofHours(6))
                .path("/")
                .httpOnly(true);

        if (useCookieSecure) {
            builder.secure(true);
            builder.sameSite("None");
        } else {
            builder.sameSite("Lax");
        }

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        return builder.build();
    }
}
