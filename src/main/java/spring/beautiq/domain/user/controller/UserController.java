package spring.beautiq.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.s3.S3Service;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @Value("${AWS_REGION}")
    private String region;

    @Value("${S3_BUCKET}")
    private String bucket;

    @GetMapping("/success")
    public String success() {
        return "OAuth2 Login Success! JWT 쿠키가 발급되었습니다";
    }

    @Operation(summary = "사용자 정보 조회", description = "username으로 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    schema = @Schema(implementation = UserResponse.class)
            ))
    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "조회할 사용자명", required = true)
            @PathVariable("username") String username) {
        if (!userService.isAccess(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserResponse dto = userService.readOneUser(username);

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "사용자 정보 수정", description = "사용자의 기본 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(
                    mediaType = "*/*",
                    examples = @ExampleObject(value = "{\"message\": \"회원 정보가 수정되었습니다.\"}")
            ))
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "수정할 사용자명", required = true)
            @PathVariable("username") String username,
            @RequestBody UserRequest userRequest) {
        if (!userService.isAccess(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "접근 권한이 없습니다."));
        }

        userService.updateOneUser(userRequest, username);
        return ResponseEntity.ok(
                Map.of("message", "회원 정보가 수정되었습니다.")
        );
    }

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    @ApiResponse(responseCode = "200", description = "업로드 성공",
            content = @Content(
                    mediaType = "*/*",
                    examples = @ExampleObject(value = """
                {
                    "success": true,
                    "message": "프로필 이미지가 변경되었습니다.",
                    "imageUrl": "https://bucket.s3.region.amazonaws.com/550e8400-e29b-41d4-a716-446655440000"
                }
                """)
            ))
    @PutMapping("/users/{username}/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @Parameter(description = "사용자명", required = true)
            @PathVariable String username,
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {
        if (!userService.isAccess(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "접근 권한이 없습니다."));
        }

        try {
            UUID fileId = UUID.randomUUID();
            s3Service.uploadImage(file, fileId);

            String imageUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    bucket, region, fileId.toString()
            );

            userService.updateProfileImage(username, imageUrl);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "프로필 이미지가 변경되었습니다.",
                            "imageUrl", imageUrl
                    )
            );
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드에 실패했습니다."));
        }
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 계정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공",
            content = @Content(
                    mediaType = "*/*",
                    examples = @ExampleObject(value = "정상적으로 삭제되었습니다.")
            ))
    @DeleteMapping("/users/{username}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "삭제할 사용자명", required = true)
            @PathVariable("username") String username) {
        if (userService.isAccess(username)) {
            userService.deleteOneUser(username);

            return ResponseEntity.ok("정상적으로 삭제되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }
}