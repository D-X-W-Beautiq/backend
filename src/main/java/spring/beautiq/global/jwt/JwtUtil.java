package spring.beautiq.global.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtUtil {

    private Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        System.out.println("JwtUtil 생성자 호출됨");
        if (secret == null) {
            throw new IllegalArgumentException("jwt.secret 값이 null 입니다!");
        }

        byte[] byteSecret = Base64.getDecoder().decode(secret);

        key = Keys.hmacShaKeyFor(byteSecret);

    }

    public UUID getUserId(String token) {
        String userId = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("userId", String.class);
        return userId != null ? UUID.fromString(userId) : null;
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }


    public String createJwt(UUID userId, String role, Long expiredMs) {

        Claims claims = Jwts.claims();

        claims.put("userId", userId.toString());
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

}
