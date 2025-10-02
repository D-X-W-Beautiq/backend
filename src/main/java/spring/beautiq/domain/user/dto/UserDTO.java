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

    private UUID id; // 신규 추가: 저장된 사용자 식별자

    private String role;

    private String name;

    private String authKey; // 추가: provider_providerId 내부 키

    private String username; // 닉네임(표시용)

    private String email; // 신규 추가: 이메일(있을 경우)

    public static UserDTO from(UserEntity entity) {
        if (entity == null) return null;
        return UserDTO.builder()
                .id(entity.getId())
                .authKey(entity.getAuthKey())
                .username(entity.getUsername())
                .name(entity.getName())
                .role(entity.getRole())
                .email(entity.getEmail())
                .build();
    }
}
