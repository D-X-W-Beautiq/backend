package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.dto.common.ImageItem;
import spring.beautiq.domain.makeup.dto.web.*;
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
     */
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveMakeUp(
            @CurrentUserId UUID userId,
            @RequestPart("data") MakeUpSaveRequestDto makeUpSaveRequestDto
    ) {
        makeUpService.saveMakeUp(userId, makeUpSaveRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
        return makeUpService.styleRecommend(userId, sourceImage, recommendRequestDto);
    }

    /**
     * 메이크업 시뮬레이션
     */
    @PostMapping(value = "/simulation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageItem simulateMakeUp(
            @CurrentUserId UUID userId,
            @RequestPart("sourceImage") MultipartFile sourceImage,
            @RequestPart("styleImage") MultipartFile styleImage
    ) throws IOException {
        return makeUpService.simulateMakeUp(userId, sourceImage, styleImage);
    }

    /**
     * 메이크업 커스터마이즈
     */
    @PostMapping(value = "/customize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageItem customize(
            @CurrentUserId UUID userId,
            @RequestPart("data") CustomizeRequestDto customizeRequestDto
    ) throws IOException {
        return makeUpService.customize(userId, customizeRequestDto);
    }

}


