package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.dto.RecommendRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendResponseDto;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/beautiq/makeup")
@MemberGuard
public class MakeUpController {

    private final MakeUpService makeUpService;

    /**
     * 메이크업 추천 받기
     * @return recommendResponseDto
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecommendResponseDto> makeRecommend(
            @CurrentUserId UUID userId,
            @RequestPart("sourceImage") MultipartFile image,
            @RequestPart("data") RecommendRequestDto recommendRequestDto
    ) throws IOException {

        return ResponseEntity.ok(makeUpService.makeRecommend(userId, image,recommendRequestDto));
    }

    /**
     * 모든 메이크업 추천 조회
     * @return recommendResponseDto
     */
    @GetMapping()
    public ResponseEntity<RecommendResponseDto> getAllRecommend(
            @CurrentUserId UUID userId
    ) {

        return ResponseEntity.ok(makeUpService.getAllRecommend(userId));
    }

    // todo: makeUpId 받는 방식 변경
    /**
     * 찜 전환
     */
    @GetMapping("/wish/{makeupId}")
    public ResponseEntity<String> changeWish(
            @PathVariable UUID makeupId
    ) {

        String responseString = makeUpService.changeWish(makeupId);
        if (responseString == null) return ResponseEntity.noContent().build();

        return ResponseEntity.ok("wish changed to " + responseString);
    }

    /**
     * 찜 목록 조회
     * @return recommendResponseDto
     */
    @GetMapping("/wish")
    public ResponseEntity<RecommendResponseDto> getAllWish(
            @CurrentUserId UUID userId
    ) {

        return makeUpService.getAllWish(userId);
    }


}
