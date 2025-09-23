package spring.beautiq.domain.skinanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.response.MonthlySkinStatusResponse;
import spring.beautiq.domain.skinanalysis.dto.response.SkinAnalysisResponse;
import spring.beautiq.domain.skinanalysis.service.SkinAnalysisService;



import java.util.List;
import java.util.UUID;

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

        if (principal == null) return ResponseEntity.status(401).body(null);

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.createAnalysis(userId, image));
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySkinStatusResponse> getMonthlyHistory(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {

        if (principal == null) return ResponseEntity.status(401).body(null);

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getMonthlyHistory(userId, year, month));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<SkinAnalysisResponse>> getDailyHistory(
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) return ResponseEntity.status(401).body(null);

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getDailyHistory(userId));
    }

    // todo : 트렌드 차트 API 개선하기
//    @GetMapping("/trends/60days")
//    public ResponseEntity<SixtyDaySkinPointsResponse> getSixtyDayTrends(
//            @AuthenticationPrincipal OAuth2User principal) {
//
//        if (principal == null) return ResponseEntity.status(401).build();
//
//        UUID userId = principal.getAttribute("userId");
//
//        return ResponseEntity.ok(skinAnalysisService.getSixtyDayTrends(userId));
//    }
//
//    @GetMapping("/trends/yearly")
//    public ResponseEntity<YearlyDaySkinPointsResponse> getYearlyDayTrends(
//            @AuthenticationPrincipal OAuth2User principal) {
//
//        if (principal == null) return ResponseEntity.status(401).build();
//
//        UUID userId = principal.getAttribute("userId");
//
//        return ResponseEntity.ok(skinAnalysisService.getYearlyDayTrends(userId));
//    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<SkinAnalysisResponse> getAnalysis(
            @PathVariable UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) return ResponseEntity.status(401).body(null);

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getAnalysis(userId, analysisId));
    }

    @DeleteMapping("/{analysisId}")
    public ResponseEntity<Void> deleteAnalysis(
            @PathVariable UUID analysisId,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        UUID userId = principal.getAttribute("userId");

        skinAnalysisService.deleteAnalysis(userId, analysisId);
        return ResponseEntity.noContent().build();
    }

    // todo: 분석 결과 기반 화장품 추천하기
//    @GetMapping("/{analysisId}/recommendations")
//    public ResponseEntity<ProductRecommendsResponse> recommendProducts(
//            @PathVariable UUID analysisId,
//            @AuthenticationPrincipal OAuth2User principal) {
//
//        if (principal == null) return ResponseEntity.status(401).build();
//
//        UUID userId = principal.getAttribute("userId");
//
//        ProductRecommendsResponse response = skinAnalysisService.recommendProducts(userId, analysisId);
//        return ResponseEntity.ok(response);
//    }
}