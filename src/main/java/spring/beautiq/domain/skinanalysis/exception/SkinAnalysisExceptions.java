package spring.beautiq.domain.skinanalysis.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import spring.beautiq.global.exception.ErrorCode;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum SkinAnalysisExceptions implements ErrorCode {
    SKIN_ANALYSIS_NOT_FOUND("피부 분석 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SKIN_ANALYSIS_FORBIDDEN("피부 분석 정보에 접근할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN)
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
