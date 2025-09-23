package spring.beautiq.domain.auth.oauth2.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hibernate.type.descriptor.jdbc.OracleJsonBlobJdbcType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.beautiq.domain.user.dto.UserDTO;

public class CustomOAuth2User implements OAuth2User {


    private final UserDTO userDTO;

    public CustomOAuth2User(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "username", userDTO.getUsername(),
                "name", userDTO.getName(),
                "role", userDTO.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userDTO.getRole()));
    }



    @Override
    public String getName() {
        return userDTO.getName();
    }

    public String getUsername() {
        return userDTO.getUsername();
    }


}
