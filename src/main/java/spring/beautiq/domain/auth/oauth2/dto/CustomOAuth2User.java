package spring.beautiq.domain.auth.oauth2.dto;

import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.beautiq.domain.user.dto.UserDTO;

public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;
    private final Map<String, Object> attributes; // 캐시된 attribute

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
        this.attributes = buildAttributes(userDTO);
    }

    private Map<String, Object> buildAttributes(UserDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("userId", dto.getId().toString());
        if (dto.getUsername() != null) map.put("username", dto.getUsername()); // 닉네임
        if (dto.getName() != null) map.put("name", dto.getName());
        if (dto.getRole() != null) map.put("role", dto.getRole());
        if (dto.getEmail() != null) map.put("email", dto.getEmail());
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userDTO.getRole() == null ? "ROLE_USER" : userDTO.getRole();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        // Spring Security principal 고유 식별자로 userId 사용
        if (userDTO.getId() != null) return userDTO.getId().toString();
        return "anonymous";
    }
}
