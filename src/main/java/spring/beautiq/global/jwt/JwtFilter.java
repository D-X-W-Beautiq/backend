package spring.beautiq.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.user.dto.OAuth2UserDTO;


@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("=== JWT Filter Debug ===");
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Request Method: {}", request.getMethod());


        String authorization = null;

        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authorization = authHeader.substring(7);
            log.info("Token from Authorization header: {}", authorization);;
        }




        if (authorization != null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    log.debug("Found cookie: {}", cookie.getName());

                    if ("Authorization".equals(cookie.getName())) {
                        authorization = cookie.getValue();
                        break;
                    }
                }
            }
        }


        //Authorization 헤더 검증
        if (authorization == null) {
            log.warn("No token found in Authorization header or Cookie");
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰
        String token = authorization;
        //토큰 소멸 시간 검증
        try {
            if (jwtUtil.isExpired(token)) {
                log.warn("Token expired");
               filterChain.doFilter(request, response);
               return;
            }

            String userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            log.info("Token validated - UserId: {}, Username: {}, Role: {}", userId, username, role);

            OAuth2UserDTO userDTO = new OAuth2UserDTO();
            userDTO.setUserId(userId);
            userDTO.setUsername(username);
            userDTO.setRole(role);

            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

            Authentication authToken = new OAuth2AuthenticationToken(
                    customOAuth2User,
                    Collections.singletonList(new SimpleGrantedAuthority(role)),
                    "jwt"
        );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("Authentication set in SecurityContext");
    } catch (io.jsonwebtoken.JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (IllegalArgumentException e) {
            log.error("Invalid token argument: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage(), e);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
