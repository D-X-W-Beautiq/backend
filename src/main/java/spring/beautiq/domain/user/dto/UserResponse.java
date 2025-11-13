package spring.beautiq.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 응답 DTO - 사용자 정보 조회 시 반환되는 데이터")
public class UserResponse {

    @Schema(
            description = "사용자 고유 ID (UUID)",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull
    private UUID id;

    @Schema(
            description = "이메일 주소 (OAuth 로그인 시 제공된 이메일)",
            example = "hong@example.com"
    )
    @NotNull
    private String email;

    @Schema(
            description = "사용자 닉네임 (변경 가능)",
            example = "홍길동123"
    )
    @NotNull
    private String username;

    @Schema(
            description = "프로필 이미지 URL (S3 저장소 경로)",
            example = "https://beautiq-test.s3.ap-northeast-2.amazonaws.com/550e8400-e29b-41d4-a716-446655440000",
            nullable = true
    )
    private String profileImage;

    @Schema(
            description = "가입일시 (계정 생성 시각)",
            example = "2024-01-15T10:30:00"
    )
    @NotNull
    private LocalDateTime createdAt;

    @Schema(description = "OAuth 제공자", example = "kakao", allowableValues = {"kakao", "google" })
    private String provider;


}