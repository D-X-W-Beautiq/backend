package spring.beautiq.domain.skinanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.response.*;
import spring.beautiq.domain.skinanalysis.service.SkinAnalysisService;
import spring.beautiq.global.security.annotation.CurrentUserId;
import spring.beautiq.global.security.guard.MemberGuard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@MemberGuard
@RestController
@RequiredArgsConstructor
@RequestMapping("/skin-analyses")
public class SkinAnalysisController {

    private final SkinAnalysisService skinAnalysisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SkinAnalysisResponse> createAnalysis(
            @CurrentUserId UUID userId,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(skinAnalysisService.createAnalysis(userId, image));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<SkinAnalysisResponse> getAnalysis(
            @CurrentUserId UUID userId,
            @PathVariable UUID analysisId
    ) {
        return ResponseEntity.ok(skinAnalysisService.getAnalysis(userId, analysisId));
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @CurrentUserId UUID userId,
            @PathVariable UUID analysisId
    ) {
        skinAnalysisService.deleteAnalysis(userId, analysisId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySkinStatusResponse> getMonthlyHistory(
            @CurrentUserId UUID userId,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();
        return ResponseEntity.ok(skinAnalysisService.getMonthlyHistory(userId, targetYear, targetMonth));
    }

    @GetMapping("/daily")
    public ResponseEntity<DailySkinDatesResponse> getDailyDates(
            @CurrentUserId UUID userId,
            @RequestParam(value = "date", required = false) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(skinAnalysisService.getDailyDates(userId, targetDate));
    }

    @GetMapping("/latest")
    public ResponseEntity<SkinAnalysisResponse> getLatestAnalysis(
            @CurrentUserId UUID userId) {
        return ResponseEntity.ok(skinAnalysisService.getLatestAnalysis(userId));
    }

    @GetMapping("/trends/60days")
    public ResponseEntity<SixtyDaySkinPointsResponse> getSixtyDayTrends(
            @CurrentUserId UUID userId,
            @RequestParam(value = "date", required = false) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        LocalDateTime dateTime = targetDate.atStartOfDay();
        return ResponseEntity.ok(skinAnalysisService.getSixtyDayTrends(userId, dateTime));
    }

    @GetMapping("/trends/yearly")
    public ResponseEntity<YearlyDaySkinPointsResponse> getYearlyDayTrends(
            @CurrentUserId UUID userId,
            @RequestParam(value = "year", required = false) Integer year
    ) {
        int targetYear = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(skinAnalysisService.getYearlyDayTrends(userId, targetYear));
    }
}