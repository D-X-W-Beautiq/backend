package spring.beautiq.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.s3.S3Service;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.service.UserService;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API - OAuth2 로그인 후 사용 가능")
@MemberGuard
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @Value("${AWS_REGION}")
    private String region;

    @Value("${S3_BUCKET}")
    private String bucket;

    @Value("${app.oauth2.redirect:https://localhost:5173/oauth/callback}")
    private String oauthRedirect;

    @Operation(
            summary = "OAuth2 로그인 (문서용)",
            description = """
                    ⚠️ 이 엔드포인트는 문서화 목적으로만 표시됩니다.
                    
                    실제 로그인 방법:
                    1. Google 로그인: GET /oauth2/authorization/google
                    2. Kakao 로그인: GET /oauth2/authorization/kakao
                    
                    로그인 흐름:
                    1. 위 URL로 브라우저 리다이렉트
                    2. OAuth2 공급자 인증 페이지로 이동
                    3. 사용자 인증 완료 후 프론트엔드로 리다이렉트 (쿠키에 JWT 토큰 포함)
                    4. 이후 모든 요청에 자동으로 JWT 쿠키 포함
                    
                    참고: 이 엔드포인트를 직접 호출하지 마세요.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "OAuth2 인증 페이지로 리다이렉트"
                    )
            }
    )
    @GetMapping("/users/login")
    public ResponseEntity<Map<String, String>> loginDocumentation(
            @Parameter(description = "OAuth2 공급자 (google 또는 kakao)", example = "google")
            @RequestParam(required = false) String ignoredProvider) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(Map.of(
                        "error", "이 엔드포인트는 문서용입니다.",
                        "googleLogin", "/oauth2/authorization/google",
                        "kakaoLogin", "/oauth2/authorization/kakao"
                ));
    }

    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다. JWT 쿠키 인증 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = @ExampleObject(
                                            name = "성공 예시",
                                            value = """
                                                    {
                                                      "id": "550e8400-e29b-41d4-a716-446655440000",
                                                      "username": "홍길동",
                                                      "email": "hong@example.com",
                                                      "profileImage": "https://bucket.s3.region.amazonaws.com/profile.jpg",
                                                      "createdAt": "2024-01-15T10:30:00"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 - 로그인 필요",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized\"}")
                            )
                    )
            }
    )
    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getMe(@CurrentUserId UUID userId) {
        UserResponse dto = userService.readOneUserById(userId);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "OAuth 콜백 리다이렉트 (내부용)",
            description = """
                    OAuth2 인증 후 프론트엔드로 리다이렉트하는 중간 엔드포인트입니다.
                    환경 변수 app.oauth2.redirect에 설정된 URL로 자동 리다이렉트됩니다.
                    
                    - 로컬: https://localhost:5173/oauth/callback (기본값)
                    - 프로덕션: 환경 변수에 설정된 값 사용
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "302",
                            description = "프론트엔드 콜백 페이지로 리다이렉트"
                    )
            }
    )
    @GetMapping("/oauth/callback")
    public void oauthCallback(HttpServletResponse response) throws IOException {
        response.sendRedirect(oauthRedirect);
    }

    @Operation(
            summary = "로그아웃",
            description = "인증 쿠키(Authorization)를 삭제하여 로그아웃합니다. 서버 세션은 stateless이므로 쿠키만 삭제됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그아웃 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"로그아웃되었습니다.\"}")
                            )
                    )
            }
    )
    @PostMapping("/users/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "현재 로그인한 사용자의 닉네임과 이메일을 수정합니다. 프로필 이미지는 별도 엔드포인트(/users/profile-image)를 사용하세요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"회원 정보가 수정되었습니다.\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 유효성 검증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"이미 사용중인 사용자명입니다.\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized\"}")
                            )
                    )
            }
    )
    @PutMapping("/users/edit")
    public ResponseEntity<Map<String, String>> updateMyUser(
            @CurrentUserId UUID userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 사용자 정보 (닉네임과 이메일 변경 가능)",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class),
                            examples = @ExampleObject(
                                    name = "정보 변경 예시",
                                    value = "{\"username\": \"새로운닉네임\", \"email\": \"newemail@example.com\"}"
                            )
                    )
            )
            @RequestBody UserRequest userRequest) {
        userService.updateMyUser(userId, userRequest);
        return ResponseEntity.ok(Map.of("message", "회원 정보가 수정되었습니다."));
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 계정을 영구적으로 삭제합니다. 삭제 후 자동으로 로그아웃됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "탈퇴 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"message\": \"회원 탈퇴가 완료되었습니다.\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized\"}")
                            )
                    )
            }
    )
    @DeleteMapping("/users/me")
    public ResponseEntity<Map<String, String>> deleteMe(
            @CurrentUserId UUID userId,
            HttpServletResponse response) {
        userService.deleteUserById(userId);

        // 탈퇴 후 쿠키 삭제
        Cookie cookie = new Cookie("Authorization", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
    }

    @Operation(
            summary = "프로필 이미지 업로드",
            description = """
                    현재 로그인한 사용자의 프로필 이미지를 변경합니다.
                    - 이미지는 S3에 업로드되며, URL이 DB에 저장됩니다.
                    - 지원 형식: JPG, PNG, GIF 등
                    - 최대 크기: 50MB (설정에 따라 변경 가능)
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "업로드 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "성공 응답",
                                            value = """
                                                    {
                                                      "success": true,
                                                      "message": "프로필 이미지가 변경되었습니다.",
                                                      "imageUrl": "https://beautiq-test.s3.ap-northeast-2.amazonaws.com/550e8400-e29b-41d4-a716-446655440000"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 - 파일이 비어있거나 형식 오류",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"파일이 비어있습니다.\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "인증 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"Unauthorized\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류 - S3 업로드 실패",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{\"error\": \"이미지 업로드에 실패했습니다.\"}")
                            )
                    )
            }
    )
    @PostMapping(value = "/users/profile-image", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMyProfileImage(
            @CurrentUserId UUID userId,
            @Parameter(
                    description = "업로드할 프로필 이미지 파일",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "파일이 비어있습니다."));
        }
        try {
            UUID fileId = UUID.randomUUID();
            s3Service.uploadImage(file, fileId);
            String imageUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileId);
            userService.updateProfileImageById(userId, imageUrl);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "프로필 이미지가 변경되었습니다.",
                    "imageUrl", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드에 실패했습니다."));
        }
    }
}
