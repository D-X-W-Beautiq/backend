package spring.beautiq.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 수정 요청 DTO")
public class UserRequest {

    @Schema(
            description = "변경할 닉네임 (2-20자, 영문/한글/숫자 가능)",
            example = "홍길동123"
    )


    @Size(min = 2, max = 20, message = "사용자명은 2자 이상 20자 이하여야 합니다.")
    private String username;

    @Schema(
            description = "변경할 이메일 주소",
            example = "newemail@example.com"
    )
    @Email
    private String email;
}