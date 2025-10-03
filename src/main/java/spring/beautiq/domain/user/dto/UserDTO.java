package spring.beautiq.domain.user.dto;


import lombok.*;

import java.util.UUID;
import spring.beautiq.domain.user.entity.UserEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO {

    private UUID id; // 사용자 식별자

    private String role;

    private String name;

    private String username; // 닉네임(표시용)

    private String email; // 이메일(있을 경우)

    public static UserDTO from(UserEntity entity) {
        if (entity == null) return null;
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .name(entity.getName())
                .role(entity.getRole())
                .email(entity.getEmail())
                .build();
    }
}
