package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.entity.MakeUpEntity;
import spring.beautiq.domain.makeup.dto.RecommendRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendResponseDto;
import spring.beautiq.domain.makeup.repository.MakeUpRepository;
import spring.beautiq.domain.makeup.s3.S3Service;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MakeUpService {

    private final MakeUpRepository makeUpRepository;
    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;

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


        // todo: 예외처리, 피부분석이 저장이 안되어서.. 일단 보류했습니다. 유저는 잘 됩니닷.
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
//        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("Skin analysis not found"));

        MakeUpEntity makeUpEntity = MakeUpEntity.builder()
                .keywords(recommendRequestDto.getKeywords())
                .isLiked(false)
                .user(user)
//                .skinAnalysis(skinAnalysis)
                .build();

        makeUpRepository.save(makeUpEntity);

        // 이미지를 받아오고 엔티티 아이디를 파일 이름으로 설정하여 저장한다.
        // 그러면 이미지 url을 따로 저장하지 않고 사용할 수 있지 않을까..합니다 -> 가능!
        // todo: 예외 처리 (업로드 실패 시)
        s3Service.uploadImage(responseImg, makeUpEntity.getId());


        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        recommendResponseDto.getRecommendations().add(s3Service.getPreSignedUrl(String.valueOf(makeUpEntity.getId())));

        return recommendResponseDto;
    }

    public RecommendResponseDto getAllRecommend(UUID userId) {
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();

        makeUpRepository.findAllByUserId(userId).forEach(
                makeUpEntity -> {
                    recommendResponseDto.addMakeup(s3Service.getPreSignedUrl(makeUpEntity.getId().toString()));
                }
        );
        System.out.println("userId = " + userId);

        return recommendResponseDto;
    }

    @Transactional
    public String changeWish(UUID makeupId) {
        Optional<MakeUpEntity> optionalMakeUp = makeUpRepository.findById(makeupId);
        if (optionalMakeUp.isPresent()) {
            MakeUpEntity makeUpEntity = optionalMakeUp.get();
            return makeUpEntity.changeWish().toString();
        } else {
            throw new RuntimeException("Make up not found");
        }
    }

    public ResponseEntity<RecommendResponseDto> getAllWish(UUID userId) {
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();

        makeUpRepository.findAllByUserIdAndIsLiked(userId, true).forEach(makeUpEntity -> {
            recommendResponseDto.addMakeup(s3Service.getPreSignedUrl(String.valueOf(makeUpEntity.getId())));
        });

        return ResponseEntity.ok(recommendResponseDto);
    }
}
