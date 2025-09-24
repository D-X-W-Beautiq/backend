package spring.beautiq.domain.skinanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.response.*;
import spring.beautiq.domain.skinanalysis.service.SkinAnalysisService;
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
            @AuthenticationPrincipal OAuth2User principal,
            @RequestPart("image") MultipartFile image
    ) {
        UUID userId = principal.getAttribute("userId");
        return ResponseEntity.ok(skinAnalysisService.createAnalysis(userId, image));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<SkinAnalysisResponse> getAnalysis(
            @PathVariable UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal) {

        UUID userId = principal.getAttribute("userId");
        return ResponseEntity.ok(skinAnalysisService.getAnalysis(userId, analysisId));
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal) {

        UUID userId = principal.getAttribute("userId");

        skinAnalysisService.deleteAnalysis(userId, analysisId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySkinStatusResponse> getMonthlyHistory(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month
    ) {
        UUID userId = principal.getAttribute("userId");
        return ResponseEntity.ok(skinAnalysisService.getMonthlyHistory(userId, year, month));
    }

    @GetMapping("/daily")
    public ResponseEntity<DailySkinDatesResponse> getDailyDates(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("date") LocalDate date) {

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getDailyDates(userId, date));
    }

    @GetMapping("/latest")
    public ResponseEntity<SkinAnalysisResponse> getLatestAnalysis(
            @AuthenticationPrincipal OAuth2User principal) {

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getLatestAnalysis(userId));
    }

    @GetMapping("/trends/60days")
    public ResponseEntity<SixtyDaySkinPointsResponse> getSixtyDayTrends(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("date") LocalDateTime date
    ) {

        UUID userId = principal.getAttribute("userId");
        return ResponseEntity.ok(skinAnalysisService.getSixtyDayTrends(userId, date));
    }

    @GetMapping("/trends/yearly")
    public ResponseEntity<YearlyDaySkinPointsResponse> getYearlyDayTrends(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("year") Integer year
    ) {

        UUID userId = principal.getAttribute("userId");
        return ResponseEntity.ok(skinAnalysisService.getYearlyDayTrends(userId, year));
    }
}