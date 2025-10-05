package spring.beautiq.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.user.dto.OAuth2UserDTO;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //cookie들을 불러온 뒤 Authorization key에 담긴 쿠키 찾기

        String authorization = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName());
                if ("Authorization".equals(cookie.getName())) {
                    authorization = cookie.getValue();
                    break;
                }
            }
        }


        //Authorization 헤더 검증
        if (authorization == null) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료
            return;
        }

        // 토큰
        String token = authorization;
        //토큰 소멸 시간 검증
        try {
            if (jwtUtil.isExpired(token)) {
               filterChain.doFilter(request, response);
               return;
            }
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            // 잘못,변조된 토큰 -> 인증 미적용 후 다음 필터로 진행
            filterChain.doFilter(request, response);
            return;
        }


        //토큰에서 username과 role 획득
        String username= jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userDto를 생성하여 값 set
        OAuth2UserDTO userDTO = new OAuth2UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);


        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User,
                null,
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)));

        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
