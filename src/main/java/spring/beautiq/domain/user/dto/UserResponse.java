package spring.beautiq.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 응답 DTO",
        example = "{\n" +
                "  \"id\": \"1\",\n" +
                "  \"email\": \"user@example.com\",\n" +
                "  \"username\": \"홍길동\",\n" +
                "  \"profileImage\": \"https://example.com/profile.jpg\"\n" +
                "}")
public class UserResponse {

    @Schema(description = "사용자 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull
    private UUID id;

    @Schema(description = "이메일 주소", example = "user@example.com")
    @NotNull
    private String email;

    @Schema(description = "사용자명", example = "홍길동")
    @NotNull
    private String username;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
    private String profileImage;
}