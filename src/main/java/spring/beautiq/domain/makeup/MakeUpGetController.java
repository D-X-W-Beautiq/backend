package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.beautiq.domain.makeup.dto.web.MakeUpDetailResponseDto;
import spring.beautiq.domain.makeup.dto.web.MakeUpListResponseDto;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/saved/makeups")
public class MakeUpGetController {

    private final MakeUpService makeUpService;

    /**
     * 저장한 메이크업 목록 조회
     * @return recommendResponseDto
     */
    @GetMapping()
    public MakeUpListResponseDto getMakeUpList(
            @CurrentUserId UUID userId, // todo: 페이징
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return makeUpService.getMakeUpList(userId, page, size);
    }

    /**
     * 메이크업 상세 조회
     * @return recommendDetailResponseDto
     */
    @GetMapping("/{makeUpId}")
    public MakeUpDetailResponseDto getMakeUp(
            @CurrentUserId UUID userId,
            @PathVariable("makeUpId") UUID makeUpId
    ) {
        return makeUpService.getMakeUp(userId, makeUpId);
    }

    /**
     * 메이크업 삭제
     */
    @DeleteMapping("/{makeUpId}")
    public ResponseEntity<Void> deleteMakeUp(
            @CurrentUserId UUID userId,
            @PathVariable("makeUpId") UUID makeUpId
    ) {
        makeUpService.deleteMakeUp(userId, makeUpId);
        return ResponseEntity.noContent().build();
    }
}
