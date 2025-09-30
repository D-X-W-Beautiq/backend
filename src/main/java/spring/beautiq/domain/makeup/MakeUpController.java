package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.dto.MakeUpSaveRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendResponseDto;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.io.IOException;
import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@RequestMapping("/makeup")
public class MakeUpController {

    private final MakeUpService makeUpService;

    /**
     * 메이크업 저장
     * todo: makeupsavereRequestDto에 이미지 정보를 어떻게 담아올지(url? imageName?)
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveMakeUp(
            @CurrentUserId UUID userId,
            @RequestPart("data") MakeUpSaveRequestDto makeUpSaveRequestDto
    ) throws IOException {
        makeUpService.saveMakeUp(userId, makeUpSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 저장한 메이크업 목록 조회
     * @return recommendResponseDto
     */
    @GetMapping()
    public RecommendResponseDto getMakeUpList(
            @CurrentUserId UUID userId
    ) {
        return makeUpService.getMakeUpList(userId);
    }

    /**
     * 메이크업 상세 조회
     * @return recommendResponseDto
     */
    @GetMapping("/{makeupId}")
    public RecommendResponseDto getMakeUp(
            @CurrentUserId UUID userId,
            @PathVariable UUID makeupId
    ) {
        return makeUpService.getMakeUp(makeupId);
    }

    /**
     * 메이크업 삭제
     */
    @DeleteMapping("/{makeupId}")
    public ResponseEntity<Void> deleteMakeUp(
            @CurrentUserId UUID userId,
            @PathVariable UUID makeupId
    ) {
        makeUpService.deleteMakeUp(makeupId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 메이크업 스타일 추천
     * todo: 이미지와 키워드 양자택일일지 둘 다 받을지 논의 필요
     */
    @PostMapping(value = "/recommendation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecommendResponseDto styleRecommend(
            @CurrentUserId UUID userId,
            @RequestPart("sourceImage") MultipartFile sourceImage,
            @RequestPart("data") RecommendRequestDto recommendRequestDto
    ) throws IOException {
        return makeUpService.styleRecommend(sourceImage, recommendRequestDto);
    }

    /**
     * 메이크업 시뮬레이션
     */
    @PostMapping(value = "/simulation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RecommendResponseDto simulateMakeUp(
            @CurrentUserId UUID userId,
            @RequestPart("sourceImage") MultipartFile sourceImage,
            @RequestPart("styleImage") MultipartFile styleImage,
            @RequestPart("data") RecommendRequestDto recommendRequestDto
    ) throws IOException {
        return makeUpService.simulateMakeUp(sourceImage, styleImage, recommendRequestDto);
    }

    /**
     * 메이크업 커스터마이즈
     */
    @PostMapping("/customize")
    public RecommendResponseDto customize(

    ) throws IOException {
        return makeUpService.customize();
    }

}

