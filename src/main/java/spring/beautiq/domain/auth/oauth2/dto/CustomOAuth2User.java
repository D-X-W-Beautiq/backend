package spring.beautiq.domain.auth.oauth2.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.beautiq.domain.user.dto.OAuth2UserDTO;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserDTO userDTO;

    public CustomOAuth2User(OAuth2UserDTO userDTO) {
        this.userDTO = Objects.requireNonNull(userDTO, "userDTO must not be null");
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();

        if (userDTO.getUserId() != null) {
            attributes.put("userId", userDTO.getUserId());
        }
        if (userDTO.getUsername() != null) {
            attributes.put("username", userDTO.getUsername());
        }
        if (userDTO.getRole() != null) {
            attributes.put("role", userDTO.getRole());
        }

        return attributes.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = userDTO.getRole();
        if (role == null || role.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    public String getUserId() {
        return userDTO.getUserId();
    }

    @Override
    public String getName() {
        return Objects.toString(userDTO.getUsername(), "");
    }

    public String getUsername() {
        return userDTO.getUsername();
    }

}
