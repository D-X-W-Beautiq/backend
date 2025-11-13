package spring.beautiq.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OAuth2 사용자 정보 DTO")
public class OAuth2UserDTO {

    @Schema(description = "사용자 ID", example = "12345")
    private String userId;

    @Schema(description = "사용자명", example = "홍길동")
    private String username;

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private String role;

    @Schema(description = "이메일", example = "minwoo@example.com")
    private String email;

    @Schema(description = "프로필 이미지 URL")
    private String profileImage;

    @Schema(description = "OAuth 제공자", example = "kakao")
    private String provider;

}