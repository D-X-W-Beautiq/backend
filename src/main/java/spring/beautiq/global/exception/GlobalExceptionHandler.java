package spring.beautiq.global.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Map<Class<? extends Exception>, ErrorCode> EXCEPTION_ERROR_CODE_MAP = new HashMap<>();

    static {
        EXCEPTION_ERROR_CODE_MAP.put(AccessDeniedException.class, GlobalErrorCode.FORBIDDEN);
        EXCEPTION_ERROR_CODE_MAP.put(AuthenticationException.class, GlobalErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(ApiException exception) {
        return createErrorResponse(exception.getErrorCode());
    }


    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleSecurityException(Exception exception) {
        ErrorCode errorCode = EXCEPTION_ERROR_CODE_MAP.getOrDefault(
                exception.getClass(),
                GlobalErrorCode.FORBIDDEN
        );
        return createErrorResponse(errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        ErrorResponse errorResponseDTO = ErrorResponse.from(exception, Instant.now());
        HttpStatus httpStatus = GlobalErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus();

        logger.error("Unhandled exception", exception);

        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        ErrorResponse errorResponseDTO = ErrorResponse.from(errorCode, Instant.now());
        HttpStatus httpStatus = errorCode.getHttpStatus();

        return new ResponseEntity<>(errorResponseDTO, httpStatus);
    }
}