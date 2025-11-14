package spring.beautiq.domain.skinanalysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.client.AIClient;
import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;
import spring.beautiq.domain.skinanalysis.dto.common.DayPoint;
import spring.beautiq.domain.skinanalysis.dto.common.MonthPoint;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusHistory;
import spring.beautiq.domain.skinanalysis.dto.common.SkinYearFeedbackType;
import spring.beautiq.domain.skinanalysis.dto.response.*;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.skinanalysis.util.SkinAnalysisCalculator;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.global.util.ImageUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {


    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;
    private final AIClient aiClient;


    // 프런트에서 이미지 받기 → AI 서버에 이미지 넘기기 → 분석 결과 받기  → 종합 점수 산출 후 분석 결과 DB에 저장 및 프런트로 응답 반환하기
    @Transactional
    public SkinAnalysisResponse createAnalysis(
            UUID userId,
            MultipartFile image
    ) {

        // 1. userId로 User 엔티티 조회
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);

        // 이미지 유효성 검증
        if (image == null || image.isEmpty()) {
            throw SkinAnalysisExceptions.IMAGE_EMPTY.toException();
        }
        if (image.getSize() > 5_000_000) { // 5MB
            throw SkinAnalysisExceptions.IMAGE_TOO_LARGE.toException();
        }
        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw SkinAnalysisExceptions.IMAGE_INVALID_TYPE.toException();
        }

        try {
            // 2. 이미지 Base64 인코딩 및 요청 DTO 구성
            // 이미지 정규화: EXIF/메타데이터 제거하여 자동 회전 문제 방지
            byte[] normalized = ImageUtil.stripExif(image.getBytes());
            String base64 = Base64.getEncoder().encodeToString(normalized);
            SkinAnalysisAIRequest aiRequest = new SkinAnalysisAIRequest();
            aiRequest.setImageBase64(base64);

            // 3. AI 서버 호출 (추상화된 AIClient 사용)
            SkinAnalysisAIResponse aiResult = aiClient.analyzeSkin(aiRequest);

            if (aiResult == null || aiResult.getPredictions() == null) {
                throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
            }
            if (aiResult.getFeedback() == null || aiResult.getFeedback().isBlank()) {
                throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
            }

            // 4. 평균 점수 계산 (새 Predictions 구조 기반)
            SkinAnalysisAIResponse.Predictions p = aiResult.getPredictions();
            Float averageScore = SkinAnalysisCalculator.calcAverageScore(p);

            // 5. 엔티티 생성 & 저장 (엔티티 필드 변경 반영)
            SkinAnalysisEntity skinAnalysisEntity = SkinAnalysisEntity.builder()
                    .user(user)
                    .pigmentationReg(p.getPigmentationReg())
                    .moistureReg(p.getMoistureReg())
                    .elasticityReg(p.getElasticityReg())
                    .wrinkleReg(p.getWrinkleReg())
                    .poreReg(p.getPoreReg())
                    .feedback(aiResult.getFeedback())
                    .averageScore(averageScore)
                    .build();
            skinAnalysisEntity = skinAnalysisRepository.save(skinAnalysisEntity);

            // 6. 응답 변환
            return SkinAnalysisResponse.from(skinAnalysisEntity);
        } catch (RuntimeException e) {
            if (e instanceof spring.beautiq.global.exception.ApiException) throw e;
            throw new RuntimeException("AI 서버 분석 요청 실패", e);
        } catch (Exception e) {
            throw new RuntimeException("AI 서버 분석 요청 실패", e);
        }
    }

    // 월별 이력(날짜 + 상태) 조회
    @Transactional(readOnly = true)
    public MonthlySkinStatusResponse getMonthlyHistory(
            UUID userId,
            Integer year,
            Integer month
    ) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        List<SkinAnalysisEntity> analyses = skinAnalysisRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        List<SkinStatusHistory> list = analyses.stream()
                .map(a -> SkinStatusHistory.builder()
                        .skinStatus(SkinAnalysisCalculator.calcSkinStatus(a))
                        .dayDate(a.getCreatedAt().toLocalDate().toString())
                        .build())
                .collect(Collectors.toList());

        return MonthlySkinStatusResponse.of(list);
    }

    // 일별 이력(날짜 목록) 조회
    @Transactional(readOnly = true)
    public DailySkinDatesResponse getDailyDates(UUID userId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        List<SkinAnalysisEntity> analyses = skinAnalysisRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        return DailySkinDatesResponse.from(analyses);
    }

    // 가장 최근 분석 결과 조회
    @Transactional(readOnly = true)
    public SkinAnalysisResponse getLatestAnalysis(UUID userId) {

        SkinAnalysisEntity skinAnalysisEntity = skinAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        return SkinAnalysisResponse.from(skinAnalysisEntity);
    }

    // 최근 60일 이내 일별 점수 리스트 + 이번 달 평균 점수 (날짜별 집계 적용)
    @Transactional(readOnly = true)
    public SixtyDaySkinPointsResponse getSixtyDayTrends(UUID userId, LocalDateTime date) {

        // 기준 날짜(date) 기준 60일 전 ~ 기준 날짜
        LocalDateTime end = date.toLocalDate().atStartOfDay().plusDays(1); // 기준 날짜의 다음날 0시
        LocalDateTime start = end.minusDays(60); // 60일 전 0시

        // 60일 이내 분석 결과 조회
        List<SkinAnalysisEntity> analyses = skinAnalysisRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        // 최근 60일 내 4회 이상의 기록이 있어야 함
        if (analyses.size() < 4) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_TOO_SHORT_HISTORY_60DAYS.toException();
        }

        // 날짜별 그룹핑 (yyyy-MM-dd)
        Map<String, List<SkinAnalysisEntity>> byDate = analyses.stream()
                .collect(Collectors.groupingBy(a -> a.getCreatedAt().toLocalDate().toString()));

        // 날짜별 평균 점수(averageScore null 은 0으로 처리) -> DayPoint 생성, 날짜 오름차순 정렬, 최대 60개 제한
        List<DayPoint> dayPoints = byDate.entrySet().stream()
                .map(entry -> {
                    double avg = entry.getValue().stream()
                            .map(SkinAnalysisEntity::getAverageScore)
                            .filter(Objects::nonNull)
                            .mapToDouble(Float::doubleValue) // Float 스트림 처리 수정
                            .average()
                            .orElse(0);
                    int rounded = (int) Math.round(avg);
                    return DayPoint.builder()
                            .dayDate(entry.getKey())
                            .point(rounded)
                            .build();
                })
                .sorted(Comparator.comparing(DayPoint::getDayDate))
                .limit(60) // 안전하게 최대 60개 보장
                .toList();

        // 기준 날짜의 연/월로 이번 달 분석 결과만 필터링
        int year = date.getYear();
        int month = date.getMonthValue();
        String thisMonth = year + "-" + String.format("%02d", month);
        List<SkinAnalysisEntity> thisMonthAnalyses = analyses.stream()
                .filter(a -> a.getCreatedAt().getYear() == year && a.getCreatedAt().getMonthValue() == month)
                .toList();

        // 이번 달 평균 점수 계산 (null 은 제외하고 평균, 없으면 0)
        int monthAvg = thisMonthAnalyses.isEmpty() ? 0 : Math.round((float) thisMonthAnalyses.stream()
                .filter(a -> a.getAverageScore() != null)
                .mapToDouble(SkinAnalysisEntity::getAverageScore)
                .average().orElse(0));

        MonthPoint monthPoint = MonthPoint.builder()
                .monthDate(thisMonth)
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

        List<SkinAnalysisEntity> analyses = skinAnalysisRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(userId, start, end);

        // 오늘 날짜 기준 3개월 이상 예전 기록이 있는지 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3);

        boolean hasOldEnoughRecord = analyses.stream()
                .anyMatch(a -> a.getCreatedAt().isBefore(threeMonthsAgo));

        if (!hasOldEnoughRecord) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_TOO_SHORT_HISTORY_3MONTHS.toException();
        }

        // 월별로 그룹핑 및 평균 계산
        var monthMap = analyses.stream()
                .filter(a -> a.getAverageScore() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreatedAt().getYear() + "-" + String.format("%02d", a.getCreatedAt().getMonthValue()),
                        Collectors.averagingDouble(SkinAnalysisEntity::getAverageScore)
                ));

        // 월별 정렬
        List<String> sortedMonths = monthMap.keySet().stream().sorted().toList();
        // MonthPoint 리스트 생성
        List<MonthPoint> monthPoints = sortedMonths.stream()
                .map(m -> MonthPoint.builder()
                        .monthDate(m)
                        .point(Math.round(monthMap.get(m).floatValue()))
                        .build())
                .toList();

        // 첫 달, 마지막 달 평균 점수로 피드백 결정
        double first = monthMap.get(sortedMonths.get(0));
        double last = monthMap.get(sortedMonths.get(sortedMonths.size() - 1));

        SkinYearFeedbackType feedbackType = (last > first) ? SkinYearFeedbackType.UPWARD : (last < first ? SkinYearFeedbackType.DOWNWARD : SkinYearFeedbackType.FLAT);

        return YearlyDaySkinPointsResponse.builder()
                .yearlyHistory(monthPoints)
                .feedback(feedbackType.getFeedback())
                .feedbackType(feedbackType)
                .build();
    }

    // 분석 결과 단건 조회
    @Transactional(readOnly = true)
    public SkinAnalysisResponse getAnalysis(UUID userId, UUID analysisId) {

        // 2. analysisId로 분석 결과 단건 조회
        SkinAnalysisEntity skinAnalysisEntity = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysisEntity.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        return SkinAnalysisResponse.from(skinAnalysisEntity);
    }

    // 분석 결과 삭제
    @Transactional
    public void deleteAnalysis(UUID userId, UUID analysisId) {

        SkinAnalysisEntity skinAnalysisEntity = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysisEntity.getUser().getId().equals(userId)) {
            throw SkinAnalysisExceptions.SKIN_ANALYSIS_FORBIDDEN.toException();
        }

        skinAnalysisRepository.delete(skinAnalysisEntity);
    }
}
