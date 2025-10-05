package spring.beautiq.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "사용자명은 필수입니다.")
    private String username;

    private String profileImage;

    @NotBlank(message = "이메일은 피룻입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}
