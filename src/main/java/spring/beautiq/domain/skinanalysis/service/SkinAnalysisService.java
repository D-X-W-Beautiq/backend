package spring.beautiq.domain.skinanalysis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.ai.common.SkinAnalysisAI;
import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;
import spring.beautiq.domain.skinanalysis.dto.response.MonthlySkinStatusResponse;
import spring.beautiq.domain.skinanalysis.dto.response.SkinAnalysisResponse;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.skinanalysis.util.SkinAnalysisCalculator;
import spring.beautiq.domain.user.entity.User;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SkinAnalysisService {


    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final WebClient webClient;

    public SkinAnalysisService(UserRepository userRepository, SkinAnalysisRepository skinAnalysisRepository, WebClient.Builder webClientBuilder) {
        this.userRepository = userRepository;
        this.skinAnalysisRepository = skinAnalysisRepository;
        this.webClient = webClientBuilder.build();
    }



    // 프런트에서 이미지 받기 → AI 서버에 이미지 넘기기 → 분석 결과 받기  → 종합 점수 산출 후 분석 결과 DB에 저장 및 프런트로 응답 반환하기
    public SkinAnalysisResponse createAnalysis(
            UUID userId,
            MultipartFile image
    ) {

        // 1. userId로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);
        try {

            // 1. 이미지 유효성 검증
            if (image == null || image.isEmpty()) {
                throw SkinAnalysisExceptions.IMAGE_EMPTY.toException();
            }
            if (image.getSize() > 5_000_000) { // 5MB 상한 (필요 시 설정값화)
                throw SkinAnalysisExceptions.IMAGE_TOO_LARGE.toException();
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw SkinAnalysisExceptions.IMAGE_INVALID_TYPE.toException();
            }

            // 2. 이미지 Base64 인코딩
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());
            SkinAnalysisAIRequest aiRequest = new SkinAnalysisAIRequest();
            aiRequest.setSourceImageBase64(base64);

            // 2. AI 서버에 JSON 요청
            SkinAnalysisAIResponse aiResult = webClient.post()
                    .uri("/skin/analysis")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(aiRequest)
                    .retrieve()
                    .bodyToMono(SkinAnalysisAIResponse.class)
                    .block(); // 동기식

            if (aiResult == null || aiResult.getPredictions() == null)
                throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
            if (aiResult.getFeedback() == null || aiResult.getFeedback().isBlank()) {
                throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
            }

            // 3. DB 저장
            SkinAnalysisAI p = aiResult.getPredictions();
            Float averageScore = SkinAnalysisCalculator.calcAverageScore(p);
            SkinAnalysis skinAnalysis = SkinAnalysis.builder()
                    .user(user)
                    .dryness(p.getDryness())
                    .foreheadPigmentation(p.getForeheadPigmentation())
                    .cheekPigmentation(p.getCheekPigmentation())
                    .pore(p.getPore())
                    .sagging(p.getSagging())
                    .foreheadWrinkle(p.getForeheadWrinkle())
                    .glabellusWrinkle(p.getGlabellusWrinkle())
                    .perocularWrinkle(p.getPerocularWrinkle())
                    .pigmentation(p.getPigmentation())
                    .cheekPore(p.getCheekPore())
                    .foreheadMoisture(p.getForeheadMoisture())
                    .cheekMoisture(p.getCheekMoisture())
                    .chinMoisture(p.getChinMoisture())
                    .foreheadElasticity(p.getForeheadElasticityR2())
                    .cheekElasticity(p.getCheekElasticityR2())
                    .chinElasticity(p.getChinElasticityR2())
                    .perocularWrinkleRa(p.getPerocularWrinkleRa())
                    .feedback(aiResult.getFeedback())
                    .averageScore(averageScore)
                    .build();
            skinAnalysis = skinAnalysisRepository.save(skinAnalysis);

            // 4. 저장된 엔티티를 응답으로 변환
            return SkinAnalysisResponse.from(skinAnalysis);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 분석 요청 실패", e);
        }
    }

    // 입력 월에 해당하는 모든 피부 분석 데이터를 조회하고, 일별로 DaySkinStatus를 계산 후 리스트에 저장하여 반환
    public MonthlySkinStatusResponse getMonthlyHistory(
            UUID userId,
            Integer year,
            Integer month
    ) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<SkinAnalysis> analyses = skinAnalysisRepository.findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        List<SkinStatusHistory> monthlyHistory = analyses.stream()
                .map(a -> SkinStatusHistory.builder()
                        .skinStatus(SkinAnalysisCalculator.calcSkinStatus(a))
                        .createdAt(a.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());

        return MonthlySkinStatusResponse.builder().monthlyHistory(monthlyHistory).build();
    }

    // todo: FE팀에서 디자인 나오면 구현하기
    public List<SkinAnalysisResponse> getDailyHistory(UUID userId) {
        // 일별 분석 결과 리스트 조회
        return List.of();
    }



    // todo: 분석 결과 단건 조회하기
    @Transactional
    public SkinAnalysisResponse getAnalysis(UUID userId, UUID analysisId) {

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysis.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        return SkinAnalysisResponse.from(skinAnalysis);
    }


    // todo : 분석 결과 삭제하기
    public void deleteAnalysis(UUID userId, UUID analysisId) {

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysis.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        skinAnalysisRepository.delete(skinAnalysis);
    }


    // tdoo : 분석 결과 기반 화장품 추천하기
    public void getAIRecommend(UUID analysisId) {

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        // 1. analysisId로 분석 결과 단건 조회
        // 2. 분석 결과를 기반으로 AI 서버에 화장품 추천 요청
        // 3. AI 서버로부터 추천 결과 받기
        // 4. 추천 결과를 프런트로 반환
    }
}
