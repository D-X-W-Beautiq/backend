package spring.beautiq.global.exception;


import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String name();
    String getMessage();
    HttpStatus getHttpStatus();

    default ApiException toException() {

        return new ApiException(this);
    }
}
