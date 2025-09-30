package spring.beautiq.domain.makeup;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.makeup.dto.MakeUpSaveRequestDto;
import spring.beautiq.domain.makeup.entity.MakeUp;
import spring.beautiq.domain.makeup.dto.RecommendRequestDto;
import spring.beautiq.domain.makeup.dto.RecommendResponseDto;
import spring.beautiq.domain.makeup.repository.MakeUpRepository;
import spring.beautiq.domain.makeup.s3.S3Service;
import spring.beautiq.domain.user.repository.UserRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MakeUpService {

    private final MakeUpRepository makeUpRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;

    /**
     * 메이크업 저장
     */
    @Transactional
    public void saveMakeUp(UUID userId, MakeUpSaveRequestDto saveRequestDto) {
        MakeUp makeUp = MakeUp.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")))
                .imageName(saveRequestDto.getImageName()) // todo: 받아온 이미지 정보 유형에 따라 수정
                .build();
        makeUpRepository.save(makeUp);
    }

    /**
     * 저장한 메이크업 목록 조회
     */
    public RecommendResponseDto getMakeUpList(UUID userId) {
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        for (MakeUp makeUp : makeUpRepository.findAllByUserId(userId)) {
            String imageName = makeUp.getImageName();
            recommendResponseDto.addRecommendation(imageName, s3Service.getPreSignedUrl(imageName));
        }
        return recommendResponseDto;
    }

    /**
     * 메이크업 상세 조회
     */
    public RecommendResponseDto getMakeUp(UUID makeupId) {
        MakeUp makeUp = makeUpRepository.findById(makeupId).orElseThrow(() -> new RuntimeException("MakeUp not found"));
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        String imageName = makeUp.getImageName();
        recommendResponseDto.addRecommendation(imageName, s3Service.getPreSignedUrl(imageName));
        return recommendResponseDto;
    }

    /**
     * 메이크업 삭제
     */
    @Transactional
    public void deleteMakeUp(UUID makeupId) {
        MakeUp makeUp = makeUpRepository.findById(makeupId).orElseThrow(() -> new RuntimeException("MakeUp not found"));
        makeUpRepository.delete(makeUp);
    }

    /**
     * 스타일 추천
     */
    public RecommendResponseDto styleRecommend(
            MultipartFile sourceImage,
            RecommendRequestDto recommendRequestDto
    ) throws IOException {

        // todo: ai에서 스타일 추천 이미지 3장 받아오기
        String[] styleImageBase64s = new String[3];

        // Base64 -> MultipartFile 변환
        MultipartFile[] styleImages = new MultipartFile[3];

        // S3에 임시 업로드 후 URL dto에 담기
        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        for (MultipartFile styleImage : styleImages) {
            String styleImageName = s3Service.uploadImage(styleImage);
            recommendResponseDto.addRecommendation(styleImageName, s3Service.getPreSignedUrl(styleImageName));
        }

        return recommendResponseDto;
    }

    /**
     * 메이크업 시뮬레이션
     */
    public RecommendResponseDto simulateMakeUp(
            MultipartFile sourceImage,
            MultipartFile styleImage,
            RecommendRequestDto recommendRequestDto
            ) throws IOException {

        //todo: MultipartBodyBuilder로 요청 본문을 구성하고 WebClient로 AI 파트로 이미지 생성 요청
        // 이후 response에서 이미지를 꺼내와서 반환해준다.
        MultipartFile responseImg = sourceImage; // 일단 원본 저장


        // 시뮬레이션 이미지 임시 업로드
        // todo: 예외 처리 (업로드 실패 시)
        String simulatedImageName = s3Service.uploadImage(responseImg);


        RecommendResponseDto recommendResponseDto = new RecommendResponseDto();
        recommendResponseDto.addRecommendation(simulatedImageName, s3Service.getPreSignedUrl(simulatedImageName));

        return recommendResponseDto;
    }

    /**
     * 메이크업 커스터마이즈
     */
    public RecommendResponseDto customize(

    ) throws IOException {
        return null;
    }

    static MultipartFile base64ToMultipart(String base64) {
        return null;
    }
}
