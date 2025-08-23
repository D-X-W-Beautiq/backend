package spring.beautiq.makeUp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.beautiq.makeUp.dto.RecommendRequestDto;
import spring.beautiq.makeUp.dto.RecommendResponseDto;

import java.io.IOException;

@RestController
@RequestMapping("/beautiq/makeup")
@RequiredArgsConstructor
public class MakeUpController {

    private final MakeUpService makeUpService;

    /**
     * 메이크업 추천 받기
     * @param recommendRequestDto
     * @return recommendResponseDto
     */
    @PostMapping("/recommend") // todo: 유저 아이디 받아오기..?
    public ResponseEntity<RecommendResponseDto> makeRecommend(@RequestBody RecommendRequestDto recommendRequestDto) throws IOException {
        return makeUpService.makeRecommend(recommendRequestDto);
    }

    /**
     * 모든 메이크업 추천 조회
     * @return recommendResponseDto
     */
    @GetMapping("/recommend")
    public ResponseEntity<RecommendResponseDto> getAllRecommend() {
        return makeUpService.getAllRecommend();
    }

    /**
     * 찜 전환
     * @param recommendId
     */
    @GetMapping("/wish/{recommendId}")
    public ResponseEntity<Void> changeWish(@PathVariable Long recommendId) {
        return makeUpService.changeWish(recommendId);
    }

    /**
     * 찜 목록 조회
     * @return recommendResponseDto
     */
    @GetMapping("/wish")
    public ResponseEntity<RecommendResponseDto> getAllWish() {
        return makeUpService.getAllWish();
    }


}
