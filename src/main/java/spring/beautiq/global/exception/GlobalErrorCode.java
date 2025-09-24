package spring.beautiq.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("응답 처리 중, 예외가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SECURITY_USER_NOT_FOUND("로그인된 유저의 정보를 불러오는 데 실패했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EXPIRED_JWT("토큰이 만료되었거나 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("유효하지 않은 JWT입니다.", HttpStatus.UNAUTHORIZED);
    private final String message;
    private final HttpStatus httpStatus;

}
