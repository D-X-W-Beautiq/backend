package spring.beautiq.domain.skinanalysis.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import spring.beautiq.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SkinAnalysisExceptions implements ErrorCode {
    SKIN_ANALYSIS_NOT_FOUND("피부 분석 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SKIN_ANALYSIS_FORBIDDEN("피부 분석 정보에 접근할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN),
    AI_SERVER_RESPONSE_EMPTY("AI 서버 응답이 비어 있습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    SKIN_ANALYSIS_TOO_SHORT_HISTORY_60DAYS("최근 60일 내에 4회 이상의 피부 분석 기록이 필요합니다.", HttpStatus.BAD_REQUEST),
    SKIN_ANALYSIS_TOO_SHORT_HISTORY_3MONTHS("3개월 이상의 장기 피부 분석 기록이 필요합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_EMPTY("이미지 파일이 비어 있습니다.", HttpStatus.BAD_REQUEST),
    IMAGE_TOO_LARGE("이미지 파일 크기가 5MB를 초과합니다.", HttpStatus.BAD_REQUEST),
    IMAGE_INVALID_TYPE("이미지 파일만 업로드할 수 있습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus httpStatus;
}
