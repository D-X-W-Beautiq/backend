package spring.beautiq.domain.auth.oauth2.successhandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor; // 추가
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.service.UserAuthService; // 공통 upsert 서비스
import spring.beautiq.global.jwt.JwtUtil;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final long TOKEN_EXPIRE_HOURS = 60L; // 60시간
    private static final int COOKIE_MAX_AGE_SEC = 60 * 30; // 30분
    private static final String REDIRECT_SUCCESS = "http://localhost:8080/success";

    private final JwtUtil jwtUtil;
    private final UserAuthService userAuthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        String role = firstAuthority(authentication);
        String authKey;

        if (principal instanceof OidcUser oidcUser) { // Google OIDC
            UserEntity user = userAuthService.upsert(
                    "google",
                    oidcUser.getSubject(),
                    safeName(oidcUser),
                    oidcUser.getEmail(),
                    role
            );
            authKey = user.getAuthKey();
        } else { // CustomOAuth2User (Kakao 등)
            authKey = authentication.getName();
        }

        String token = jwtUtil.createJwt(authKey, role, TimeUnit.HOURS.toMillis(TOKEN_EXPIRE_HOURS));
        response.addCookie(cookie(token));
        response.sendRedirect(REDIRECT_SUCCESS);
    }

    private String safeName(OidcUser user) {
        String full = user.getFullName();
        if (full != null && !full.isBlank()) return full;
        String attr = user.getAttribute("name");
        return attr != null ? attr : "USER";
    }

    private String firstAuthority(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null || authorities.isEmpty()) return "ROLE_USER";
        return authorities.iterator().next().getAuthority();
    }

    private Cookie cookie(String value) {
        Cookie c = new Cookie("Authorization", value);
        c.setMaxAge(COOKIE_MAX_AGE_SEC);
        c.setPath("/");
        c.setHttpOnly(false); // 정책 유지
        return c;
    }
}
