package spring.beautiq.global.exception;


import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private Integer status;
    private Instant timestamp;

    public static ErrorResponse from(ErrorCode errorCode, Instant timestamp) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .status(errorCode.getHttpStatus().value())
                .timestamp(timestamp)
                .build();
    }

    public static ErrorResponse from(Exception e, Instant timestamp) {
        GlobalErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        return ErrorResponse
                .builder()
                .code(errorCode.name())
                .message(e.getClass().getSimpleName() + ": " + e.getMessage())
                .status(errorCode.getHttpStatus().value())
                .timestamp(timestamp)
                .build();
    }
}
