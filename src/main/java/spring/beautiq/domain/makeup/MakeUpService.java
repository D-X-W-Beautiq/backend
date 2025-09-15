package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.entity.MakeUp;
import spring.beautiq.domain.makeup.dto.RecommendRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendResponseDto;
import spring.beautiq.domain.makeup.repository.MakeUpRepository;
import spring.beautiq.domain.makeup.s3.S3Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MakeUpService {

    private final MakeUpRepository makeUpRepository;
    private final S3Service s3Service;

    @Transactional
    public RecommendResponseDto makeRecommend(
            UUID userId,
            MultipartFile image,
            RecommendRequestDto recommendRequestDto
            ) throws IOException {

        //todo: MultipartBodyBuilder로 요청 본문을 구성하고 WebClient로 AI 파트로 이미지 생성 요청
        // 이후 response에서 이미지를 꺼내와서 반환해준다.
        MultipartFile responseImg = image; // 일단 원본 저장


        // todo: 유저, 피부분석 매핑
        MakeUp makeUp = MakeUp.builder()
                .keywords(recommendRequestDto.getKeywords())
                .isLiked(false)
                .build();

        makeUpRepository.save(makeUp);

        // 이미지를 받아오고 엔티티 아이디를 파일 이름으로 설정하여 저장한다.
        // 그러면 이미지 url을 따로 저장하지 않고 사용할 수 있지 않을까..합니다 -> 가능!
        // todo: 예외 처리 (업로드 실패 시)
        s3Service.uploadImage(responseImg, makeUp.getId());


        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        recommendResponseDto.getRecommendations().add(s3Service.getPreSignedUrl(String.valueOf(makeUp.getId())));

        return recommendResponseDto;
    }

    public RecommendResponseDto getAllRecommend(UUID userId) {
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        // todo: User 매핑 후, userId로 다 찾고 dto 만들기
        makeUpRepository.findAll().forEach(
                makeUp -> { // 지금은 전부 리턴
                    recommendResponseDto.addMakeup(s3Service.getPreSignedUrl(makeUp.getId().toString()));
                }
        );
        System.out.println("userId = " + userId);

        return recommendResponseDto;
    }

    @Transactional
    public String changeWish(UUID makeupId) {
        Optional<MakeUp> optionalMakeUp = makeUpRepository.findById(makeupId);
        if (optionalMakeUp.isPresent()) {
            MakeUp makeUp = optionalMakeUp.get();
            return makeUp.changeWish().toString();
        } else {
            return null;
        }
    }

    public ResponseEntity<RecommendResponseDto> getAllWish(UUID userId) {
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        // todo: User 매핑 후 userId로 찾기
        makeUpRepository.findAll().forEach(makeUp -> { // 지금은 찜 되어있는 객체 전부 리턴
            if (makeUp.getIsLiked()) {
                recommendResponseDto.addMakeup(s3Service.getPreSignedUrl(String.valueOf(makeUp.getId())));
            }
        });
        return ResponseEntity.ok(recommendResponseDto);
    }
}
