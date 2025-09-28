package spring.beautiq.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import spring.beautiq.domain.user.dto.UserRequest;
import spring.beautiq.domain.user.dto.UserResponse;
import spring.beautiq.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/success")
    public String success() {
        return "OAuth2 Login Success! JWT 쿠키가 발급되었습니다 🚀";
    }

    //회원 가입
    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@RequestBody UserRequest userRequest) {
        userService.cretaeOneUser(userRequest);

        return ResponseEntity.ok("회원가입 성공");
    }

    //회원 정보 조회
    @GetMapping("/users/update/{username}")
    public ResponseEntity<?> getUser(@PathVariable("usernmae") String username) {
        if (userService.isAccess(username)) {
            UserResponse dto = userService.readOneUser(username);
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }

    //회원 수정
    @PostMapping("/users/update/{username}")
    public ResponseEntity<?> updateUser(@PathVariable("usernmae") String username,
                                        @RequestBody UserRequest userRequest) {
        if (userService.isAccess(username)) {
            userService.updateOneUser(userRequest, username);
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근 권한이 없습니다.");
    }
}