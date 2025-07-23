package spring.beautiq.global.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ApiException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponseDTO = ErrorResponse.from(errorCode, Instant.now());
        HttpStatus httpStatus = errorCode.getHttpStatus();

        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponseDTO = ErrorResponse.from(exception, Instant.now());
        HttpStatus httpStatus = errorCode.getHttpStatus();

        exception.printStackTrace();

        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }
}