package spring.beautiq.global.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(readOnly = true)
public class JwtUtil {

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        System.out.println("JwtUtil 생성자 호출됨");
        if (secret == null) {
            throw new IllegalArgumentException("jwt.secret 값이 null 입니다!");
        }

        byte[] byteSecret = Base64.getDecoder().decode(secret);

        key = Keys.hmacShaKeyFor(byteSecret);

    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("userId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("email", String.class);
    }

    public String getProfileImage(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("profileImage", String.class);
    }

    public String getProvider(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("provider", String.class);
    }


    public Boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }


    public String createJwt(String userId, String username, String role, Long expiredMs, String email, String profileImage, String provider) {

        Claims claims = Jwts.claims();

        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        if (email != null) claims.put("email", email);
        if (profileImage != null) claims.put("profileImage", profileImage);
        if (provider != null) claims.put("provider", provider);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    // 기존 호출 호환성 유지용 오버로드: email/profileImage/provider 없이 간단히 토큰 생성
    public String createJwt(String userId, String username, String role, Long expiredMs) {
        return createJwt(userId, username, role, expiredMs, null, null, null);
    }

}
