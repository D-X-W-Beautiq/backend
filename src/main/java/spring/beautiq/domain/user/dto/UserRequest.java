package spring.beautiq.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 요청 DTO",
        example = "{\n" +
                "  \"username\": \"홍길동\",\n" +
                "  \"profileImage\": \"https://example.com/profile.jpg\",\n" +
                "  \"email\": \"user@example.com\"\n" +
                "}")
public class UserRequest {

    @Schema(description = "사용자명", example = "홍길동")
    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
    private String profileImage;

    @Schema(description = "이메일 주소", example = "user@example.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}