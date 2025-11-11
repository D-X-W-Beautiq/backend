package spring.beautiq.domain.skinanalysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.response.*;
import spring.beautiq.domain.skinanalysis.service.SkinAnalysisService;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@RequestMapping("/skin-analyses")
@Tag(name = "Skin Analysis", description = "피부 분석 API - 이미지 업로드 및 분석 결과 조회")
public class SkinAnalysisController {

    private final SkinAnalysisService skinAnalysisService;

    @Operation(
            summary = "피부 분석 생성",
            description = """
                    사용자의 피부 이미지를 AI로 분석하여 결과를 저장합니다.
                    
                    **흐름:**
                    1. 프론트에서 이미지 파일 업로드
                    2. 백엔드가 S3에 저장 후 AI 서버에 분석 요청
                    3. AI 분석 결과를 DB에 저장
                    4. 분석 결과 반환 (피부 타입, 수분도, 유분도, 색소침착 등)
                    
                    **분석 항목:**
                    - 피부 타입 (건성/지성/복합성/민감성)
                    - 수분도, 유분도, 색소침착, 주름, 모공, 트러블 점수
                    - AI 피드백 메시지
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "분석 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SkinAnalysisResponse.class),
                            examples = @ExampleObject(
                                    name = "분석 결과",
                                    value = """
                                            {
                                              "id": "550e8400-e29b-41d4-a716-446655440000",
                                              "userId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                              "skinAnalysis": {
                                                "pigmentationReg": 45,
                                                "moistureReg": 65,
                                                "elasticityReg": 78,
                                                "wrinkleReg": 30,
                                                "poreReg": 55
                                              },
                                              "feedback": "전반적으로 건조한 피부 타입으로 보습 관리가 필요합니다.",
                                              "averageScore": 54.6,
                                              "createdAt": "2025-01-10T10:30:00Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 입력 - 이미지 파일 누락 또는 형식 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "IMAGE_INVALID_TYPE",
                                              "message": "이미지 파일이 필요합니다. JPG 또는 PNG 형식만 지원됩니다.",
                                              "status": 400,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "AI 서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "AI_SERVER_RESPONSE_EMPTY",
                                              "message": "AI 분석 서버와 통신 중 오류가 발생했습니다.",
                                              "status": 500,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SkinAnalysisResponse> createAnalysis(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @RequestPart("image")
            @Parameter(description = "피부 이미지 파일 (JPG/PNG)", required = true)
            MultipartFile image
    ) {
        return ResponseEntity.ok(skinAnalysisService.createAnalysis(userId, image));
    }

    @Operation(
            summary = "피부 분석 결과 조회",
            description = "특정 분석 ID의 상세 결과를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SkinAnalysisResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "다른 사용자의 분석 결과 접근 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_FORBIDDEN",
                                              "message": "해당 피부 분석 결과에 접근할 권한이 없습니다.",
                                              "status": 403,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "분석 결과를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_NOT_FOUND",
                                              "message": "피부 분석 결과를 찾을 수 없습니다.",
                                              "status": 404,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{analysisId}")
    public ResponseEntity<SkinAnalysisResponse> getAnalysis(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "분석 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID analysisId
    ) {
        return ResponseEntity.ok(skinAnalysisService.getAnalysis(userId, analysisId));
    }

    @Operation(
            summary = "피부 분석 결과 삭제",
            description = "특정 분석 결과를 삭제합니다. S3 이미지도 함께 삭제됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "삭제 성공 - 응답 본문 없음"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증이 필요합니다.",
                                              "status": 401,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "다른 사용자의 분석 결과 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_FORBIDDEN",
                                              "message": "해당 피부 분석 결과를 삭제할 권한이 없습니다.",
                                              "status": 403,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "분석 결과를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "SKIN_ANALYSIS_NOT_FOUND",
                                              "message": "피부 분석 결과를 찾을 수 없습니다.",
                                              "status": 404,
                                              "timestamp": "2025-01-10T10:30:00.123Z"
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "분석 ID (UUID)")
            @PathVariable UUID analysisId
    ) {
        skinAnalysisService.deleteAnalysis(userId, analysisId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "월별 피부 상태 조회",
            description = """
                    특정 월의 일별 피부 분석 요약 정보를 조회합니다.
                    
                    **반환 정보:**
                    - 해당 월의 각 날짜별로 분석이 있는지 여부
                    - 각 날짜의 대표 피부 점수
                    - 캘린더 UI 구성에 활용
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MonthlySkinStatusResponse.class)))
    })
    @GetMapping("/monthly")
    public ResponseEntity<MonthlySkinStatusResponse> getMonthlyHistory(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "조회할 연도 (기본: 현재 연도)", example = "2025")
            @RequestParam(value = "year", required = false) Integer year,
            @Parameter(description = "조회할 월 (1-12, 기본: 현재 월)", example = "1")
            @RequestParam(value = "month", required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();
        return ResponseEntity.ok(skinAnalysisService.getMonthlyHistory(userId, targetYear, targetMonth));
    }

    @Operation(
            summary = "특정 일자의 분석 타임스탬프 조회",
            description = """
                    특정 날짜에 수행된 모든 피부 분석의 시간 목록을 반환합니다.
                    
                    **사용 사례:**
                    - 하루에 여러 번 분석한 경우 각 분석 시간 표시
                    - 사용자가 원하는 시간대의 분석 선택 가능
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = DailySkinDatesResponse.class)))
    })
    @GetMapping("/daily")
    public ResponseEntity<DailySkinDatesResponse> getDailyDates(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "조회할 날짜 (기본: 오늘)", example = "2025-01-15")
            @RequestParam(value = "date", required = false) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(skinAnalysisService.getDailyDates(userId, targetDate));
    }

    @Operation(
            summary = "최신 피부 분석 결과 조회",
            description = """
                    사용자의 가장 최근 피부 분석 결과를 반환합니다.
                    
                    **사용 사례:**
                    - 홈 화면에 최신 피부 상태 표시
                    - 제품 추천 시 최신 분석 데이터 활용
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "분석 기록이 없음")
    })
    @GetMapping("/latest")
    public ResponseEntity<SkinAnalysisResponse> getLatestAnalysis(
            @Parameter(hidden = true) @CurrentUserId UUID userId) {
        return ResponseEntity.ok(skinAnalysisService.getLatestAnalysis(userId));
    }

    @Operation(
            summary = "60일 피부 트렌드 조회",
            description = """
                    최근 60일간의 피부 점수 변화 추이를 조회합니다.
                    
                    **반환 정보:**
                    - 일별 수분도, 유분도, 색소침착, 주름, 모공, 트러블 점수
                    - 그래프 차트 구성에 활용
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = SixtyDaySkinPointsResponse.class)))
    })
    @GetMapping("/trends/60days")
    public ResponseEntity<SixtyDaySkinPointsResponse> getSixtyDayTrends(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "기준 날짜 (기본: 오늘, 이 날짜 기준 과거 60일)", example = "2025-01-15")
            @RequestParam(value = "date", required = false) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime dateTime = targetDate.atStartOfDay();
        return ResponseEntity.ok(skinAnalysisService.getSixtyDayTrends(userId, dateTime));
    }

    @Operation(
            summary = "연간 피부 트렌드 조회",
            description = """
                    특정 연도의 월별 평균 피부 점수를 조회합니다.
                    
                    **반환 정보:**
                    - 각 월의 평균 수분도, 유분도, 색소침착, 주름, 모공, 트러블 점수
                    - 연간 피부 상태 변화 추이 파악
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = YearlyDaySkinPointsResponse.class)))
    })
    @GetMapping("/trends/yearly")
    public ResponseEntity<YearlyDaySkinPointsResponse> getYearlyDayTrends(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Parameter(description = "조회할 연도 (기본: 현재 연도)", example = "2025")
            @RequestParam(value = "year", required = false) Integer year
    ) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(skinAnalysisService.getYearlyDayTrends(userId, targetYear));
    }
}