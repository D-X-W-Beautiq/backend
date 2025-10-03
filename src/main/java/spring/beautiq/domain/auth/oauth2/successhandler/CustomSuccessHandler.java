package spring.beautiq.domain.auth.oauth2.successhandler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.global.jwt.JwtUtil;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final long TOKEN_EXPIRE_HOURS = 60L; // 60시간
    private static final int COOKIE_MAX_AGE_SEC = 60 * 30; // 30분
    private static final String REDIRECT_SUCCESS = "http://localhost:8080/success";

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomOAuth2User customUser)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication principal");
            return;
        }

        String role = firstAuthority(authentication);
        UUID userId = UUID.fromString(customUser.getName()); // getName()은 userId를 반환

        String token = jwtUtil.createJwt(userId, role, TimeUnit.HOURS.toMillis(TOKEN_EXPIRE_HOURS));
        response.addCookie(cookie(token));
        response.sendRedirect(REDIRECT_SUCCESS);
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
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setAttribute("SameSite", "None");
        return c;
    }
}
