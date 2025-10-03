package spring.beautiq.domain.auth.oauth2.dto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.beautiq.domain.user.dto.OAuth2UserDTO;

public class CustomOAuth2User implements OAuth2User {


    private final OAuth2UserDTO userDTO;

    public CustomOAuth2User(OAuth2UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "userId", userDTO.getUserId(),
                "username", userDTO.getUsername(),
                "name", userDTO.getName(),
                "role", userDTO.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDTO.getRole()));
    }


    public String getUserId() {
        return userDTO.getUserId();
    }

    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getUsername() {
        return userDTO.getUsername();
    }


}
