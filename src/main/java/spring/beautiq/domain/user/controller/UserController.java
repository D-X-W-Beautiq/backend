package spring.beautiq.domain.user.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @Value("${AWS_REGION}")
    private String region;

    @Value("${S3_BUCKET}")
    private String bucket;

    @GetMapping("/success")
    public String success() {
        return "OAuth2 Login Success! JWT 쿠키가 발급되었습니다 🚀";
    }

    //회원 정보 조회
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUser(@PathVariable("username") String username) {
        if (!userService.isAccess(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "접근 권한이 없습니다."));
        }
        UserResponse dto = userService.readOneUser(username);

        return ResponseEntity.ok(dto);
    }

    //회원 수정
    @PutMapping("/users/{username}")
    public ResponseEntity<?> updateUser(@PathVariable("username") String username,
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

    @PutMapping("/users/{username}/profile-image")
    public ResponseEntity<?> uploadProfileImage(@PathVariable String username,
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



    //회원 탈퇴
    @DeleteMapping("/users/{username}}")
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username) {
        if (userService.isAccess(username)) {
            userService.deleteOneUser(username);

            return ResponseEntity.ok("정상적으로 삭제되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }


}