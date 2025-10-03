package spring.beautiq.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.user.dto.UserDTO;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTH_COOKIE = "Authorization";
    private static final Set<String> PUBLIC_EXACT = Set.of("/", "/login", "/error");
    private static final String[] PUBLIC_PREFIXES = {
            "/oauth2/authorization", "/success", "/css", "/js", "/getpostman.com", "/auth", "/login/oauth2"
    };

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String token = resolveToken(request);
        boolean publicPath = isPublic(uri) || isPreflight(request);

        if (token == null) {
            if (publicPath) { chain.doFilter(request, response); return; }
            writeError(response, GlobalErrorCode.INVALID_ACCESS_TOKEN); return;
        }

        GlobalErrorCode validation = validate(token);
        if (validation != null) { // 실패
            if (publicPath) { chain.doFilter(request, response); return; }
            writeError(response, validation); return;
        }

        buildAuthentication(token).ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth));
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(c -> AUTH_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    private GlobalErrorCode validate(String token) {
        try {
            if (jwtUtil.isExpired(token)) return GlobalErrorCode.INVALID_EXPIRED_JWT;
            return null; // OK
        } catch (Exception e) {
            return GlobalErrorCode.INVALID_JWT; // 파싱/서명 실패
        }
    }

    private boolean isPreflight(HttpServletRequest req) { return "OPTIONS".equalsIgnoreCase(req.getMethod()); }

    private boolean isPublic(String uri) {
        if (uri == null) return false;
        if (PUBLIC_EXACT.contains(uri)) return true;
        for (String prefix : PUBLIC_PREFIXES) if (uri.startsWith(prefix)) return true;
        return false;
    }

    private Optional<UsernamePasswordAuthenticationToken> buildAuthentication(String token) {
        try {
            UUID userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                // 사용자가 삭제되거나 존재하지 않으면 인증 실패
                return Optional.empty();
            }

            UserDTO dto = UserDTO.from(user);
            if (dto == null) {
                // DTO 변환 실패 시에도 인증 실패
                return Optional.empty();
            }

            CustomOAuth2User principal = new CustomOAuth2User(dto);
            return Optional.of(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private void writeError(HttpServletResponse response, GlobalErrorCode code) throws IOException {
        response.setStatus(code.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        Instant now = Instant.now();
        String body = '{' + "\"code\":\"" + code.name() + "\"," +
                "\"message\":\"" + code.getMessage().replace("\"", "'") + "\"," +
                "\"status\":" + code.getHttpStatus().value() + ',' +
                "\"timestamp\":\"" + now + "\"}";
        response.getWriter().write(body);
    }
}
