package spring.beautiq.domain.skinanalysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.ai.common.SkinAnalysisAI;
import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;
import spring.beautiq.domain.skinanalysis.dto.common.DayPoint;
import spring.beautiq.domain.skinanalysis.dto.common.MonthPoint;
import spring.beautiq.domain.skinanalysis.dto.common.SkinYearFeedbackType;
import spring.beautiq.domain.skinanalysis.dto.response.*;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.skinanalysis.util.SkinAnalysisCalculator;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {


    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final WebClient.Builder webClientBuilder;


    // 프런트에서 이미지 받기 → AI 서버에 이미지 넘기기 → 분석 결과 받기  → 종합 점수 산출 후 분석 결과 DB에 저장 및 프런트로 응답 반환하기
    @Transactional
    public SkinAnalysisResponse createAnalysis(
            UUID userId,
            MultipartFile image
    ) {

        // 1. userId로 User 엔티티 조회
        UserEntity user = userRepository.findById(userId)
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
            SkinAnalysisAIResponse aiResult = webClientBuilder.build().post()
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
    @Transactional(readOnly = true)
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

    // 일별 분석 결과 리스트 조회: 해당 날짜 00:00 ~ 다음날 00:00
    @Transactional(readOnly = true)
    public DailySkinDatesResponse getDailyDates(UUID userId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<SkinAnalysis> analyses = skinAnalysisRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        return DailySkinDatesResponse.from(analyses);
    }

    // 가장 최근 분석 결과 조회
    @Transactional(readOnly = true)
    public SkinAnalysisResponse getLatestAnalysis(UUID userId) {

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        return SkinAnalysisResponse.from(skinAnalysis);
    }

    // 최근 60일 이내 일별 점수 리스트 + 이번 달 평균 점수
    @Transactional(readOnly = true)
    public SixtyDaySkinPointsResponse getSixtyDayTrends(UUID userId, LocalDateTime date) {

        // 기준 날짜(date) 기준 60일 전 ~ 기준 날짜
        LocalDateTime end = date.toLocalDate().atStartOfDay().plusDays(1); // 기준 날짜의 다음날 0시
        LocalDateTime start = end.minusDays(60); // 60일 전 0시

        // 60일 이내 분석 결과 조회
        List<SkinAnalysis> analyses = skinAnalysisRepository.findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                userId, start, end);

        // 일별 점수 리스트 생성
        List<DayPoint> dayPoints = analyses.stream()
                .map(a -> DayPoint.builder()
                        .date(a.getCreatedAt().toLocalDate().toString())
                        .point(a.getAverageScore() != null ? Math.round(a.getAverageScore()) : 0)
                        .build())
                .toList();

        // 기준 날짜의 연/월로 이번 달 분석 결과만 필터링
        int year = date.getYear();
        int month = date.getMonthValue();
        String thisMonth = year + "-" + String.format("%02d", month);
        List<SkinAnalysis> thisMonthAnalyses = analyses.stream()
                .filter(a -> {
                    LocalDateTime created = a.getCreatedAt();
                    return created.getYear() == year && created.getMonthValue() == month;
                })
                .toList();

        // 이번 달 평균 점수 계산
        int monthAvg = thisMonthAnalyses.isEmpty() ? 0 :
                Math.round((float) thisMonthAnalyses.stream()
                        .filter(a -> a.getAverageScore() != null)
                        .mapToDouble(SkinAnalysis::getAverageScore)
                        .average().orElse(0));

        MonthPoint monthPoint =
                MonthPoint.builder()
                        .month(thisMonth)
                        .point(monthAvg)
                        .build();

        return SixtyDaySkinPointsResponse.builder()
                .within60Days(dayPoints)
                .currentMonth(monthPoint)
                .build();
    }

    // 최근 1년 이내 월별 점수 리스트 + 피드백
    @Transactional(readOnly = true)
    public YearlyDaySkinPointsResponse getYearlyDayTrends(UUID userId, Integer year) {

        // 해당 연도 1월 1일 ~ 12월 31일 23:59:59
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(year + 1, 1, 1, 0, 0);

        List<SkinAnalysis> analyses = skinAnalysisRepository.findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                userId, start, end);

        // 월별로 그룹핑 및 평균 계산
        var monthMap = analyses.stream()
                .filter(a -> a.getAverageScore() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreatedAt().getYear() + "-" + String.format("%02d", a.getCreatedAt().getMonthValue()),
                        Collectors.averagingDouble(SkinAnalysis::getAverageScore)
                ));

        // 월별 정렬
        List<String> sortedMonths = monthMap.keySet().stream().sorted().toList();
        // 3개월 이상 기록이 없으면 예외
        if (sortedMonths.size() < 3) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_TOO_SHORT_HISTORY.toException();
        }
        // MonthPoint 리스트 생성
        List<MonthPoint> monthPoints = sortedMonths.stream()
                .map(month -> MonthPoint.builder()
                        .month(month)
                        .point(Math.round(monthMap.get(month).floatValue()))
                        .build())
                .toList();

        // 첫 달, 마지막 달 평균 점수로 피드백 결정
        double first = monthMap.get(sortedMonths.get(0));
        double last = monthMap.get(sortedMonths.get(sortedMonths.size() - 1));

        SkinYearFeedbackType feedbackType;
        if (last > first) {
            feedbackType = SkinYearFeedbackType.UPWARD;
        } else if (last < first) {
            feedbackType = SkinYearFeedbackType.DOWNWARD;
        } else {
            feedbackType = SkinYearFeedbackType.FLAT;
        }

        return YearlyDaySkinPointsResponse.builder()
                .yearlyHistory(monthPoints)
                .feedback(feedbackType.getFeedback())
                .build();
    }

    // 분석 결과 단건 조회
    @Transactional(readOnly = true)
    public SkinAnalysisResponse getAnalysis(UUID userId, UUID analysisId) {

        // 2. analysisId로 분석 결과 단건 조회
        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysis.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        return SkinAnalysisResponse.from(skinAnalysis);
    }

    // 분석 결과 삭제
    @Transactional
    public void deleteAnalysis(UUID userId, UUID analysisId) {

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysis.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        skinAnalysisRepository.delete(skinAnalysis);
    }
}
