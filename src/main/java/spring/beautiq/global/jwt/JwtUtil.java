package spring.beautiq.global.jwt;


import static spring.beautiq.global.exception.GlobalErrorCode.INVALID_EXPIRED_JWT;
import static spring.beautiq.global.exception.GlobalErrorCode.INVALID_JWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import spring.beautiq.global.exception.BusinessException;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtUtil {

    private Key key;

    public JwtUtil(@Value("${jwt.secret}")String secret) {
        byte[] byteSecret = Base64.getDecoder().decode(secret);

        key = Keys.hmacShaKeyFor(byteSecret);
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }


    public String createJwt(String username, String role, Long expiredMs) {

        Claims claims = Jwts.claims();

        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

}
