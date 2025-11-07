package spring.beautiq.domain.makeup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.dto.web.*;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.io.IOException;
import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@RequestMapping("/makeup")
@Tag(name = "Makeup", description = "메이크업 추천/시뮬레이션/커스터마이즈 API - Base64 기반")
public class MakeUpController {

    private final MakeUpService makeUpService;

    /**
     * 메이크업 저장
     */
    @Operation(
            summary = "메이크업 저장",
            description = """
                    시뮬레이션 또는 커스터마이징 결과 이미지(Base64)를 S3에 영구 저장하고 DB에 기록합니다.
                    
                    **흐름:**
                    1. 프론트에서 Base64 이미지 전송
                    2. 백엔드가 S3 images 폴더에 저장
                    3. DB에 메이크업 기록 저장
                    
                    **요청:**
                    - Content-Type: application/json
                    - imageBase64: Base64 인코딩된 이미지 문자열
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "저장 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/save")
    public ResponseEntity<Void> saveMakeUp(
            @Parameter(hidden = true) @CurrentUserId UUID userId,
            @Valid @RequestBody MakeUpSaveRequestDto makeUpSaveRequestDto
    ) {
        makeUpService.saveMakeUp(userId, makeUpSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 메이크업 스타일 추천
     */
    @Operation(
            summary = "메이크업 스타일 추천",
            description = """
                    사용자 얼굴 이미지 파일을 받아 3개의 추천 스타일을 Base64로 반환합니다.
                    
                    **흐름:**
                    1. 프론트에서 파일 업로드
                    2. 백엔드에서 Base64 변환하여 AI 서버 요청
                    3. AI 서버에서 3개 스타일 Base64로 응답
                    4. 프론트는 Base64를 저장 (추후 시뮬레이션에 사용)
                    
                    **S3 저장 없음** - 프론트 메모리에만 존재
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 성공",
                    content = @Content(schema = @Schema(implementation = RecommendResponseDto.class)))
    })
    @PostMapping(value = "/recommendation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecommendResponseDto styleRecommend(
            @RequestPart(name = "sourceImage")
            @Parameter(description = "사용자 얼굴 이미지 파일 (JPG/PNG)")
            MultipartFile sourceImage,

            @Valid @RequestPart(name = "data")
            @Parameter(description = "키워드 (선택, 최대 5개)")
            RecommendRequestDto recommendRequestDto
    ) throws IOException {
        return makeUpService.styleRecommend(sourceImage, recommendRequestDto);
    }

    /**
     * 메이크업 시뮬레이션
     */
    @Operation(
            summary = "메이크업 시뮬레이션",
            description = """
                    원본 이미지(Base64)와 참조 스타일(파일 또는 Base64)을 받아 메이크업을 적용한 결과를 Base64로 반환합니다.
                    
                    **흐름:**
                    1. 프론트: 원본 Base64 + (추천 이미지 Base64 선택 OR 새 파일 업로드)
                    2. 백엔드: AI 서버에 시뮬레이션 요청
                    3. 백엔드: 결과 Base64 반환
                    
                    **S3 저장 없음** - 프론트 메모리에만 존재
                    """
    )
    @PostMapping(value = "/simulation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimulationResponseDto simulateMakeUp(
            @Valid @RequestPart(name = "data")
            @Parameter(description = "시뮬레이션 요청 데이터 (원본 Base64 + 참조 이미지)")
            SimulationRequestDto requestDto
    ) throws IOException {
        return makeUpService.simulateMakeUp(requestDto);
    }

    /**
     * 메이크업 커스터마이즈
     */
    @Operation(
            summary = "메이크업 커스터마이즈",
            description = """
                    시뮬레이션 결과 이미지(Base64)의 색상/강도를 조정한 결과를 Base64로 반환합니다.
                    
                    **흐름:**
                    1. 프론트: 시뮬레이션 Base64 + 편집 조건
                    2. 백엔드: AI 서버에 커스터마이즈 요청
                    3. 백엔드: 결과 Base64 반환
                    4. (반복 가능)
                    
                    **S3 저장 없음** - save API 호출 전까지 프론트 메모리에만 존재
                    """
    )
    @PostMapping(value = "/customize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public CustomizeResponseDto customize(
            @Valid @RequestBody CustomizeRequestDto customizeRequestDto
    ) {
        return makeUpService.customize(customizeRequestDto);
    }

}

