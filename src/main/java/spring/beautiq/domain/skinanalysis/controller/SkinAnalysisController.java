package spring.beautiq.domain.skinanalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.request.SkinAnalysisRequestDto;
import spring.beautiq.domain.skinanalysis.dto.response.SkinAnalysisResponseDto;
import spring.beautiq.domain.skinanalysis.service.SkinAnalysisService;



import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/skin-analyses")
public class SkinAnalysisController {

    private final SkinAnalysisService skinAnalysisService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SkinAnalysisResponseDto> createAnalysis(
            @AuthenticationPrincipal OAuth2User principal,
            @RequestPart("image") MultipartFile image,
            @RequestPart("data") SkinAnalysisRequestDto dto) {

        if (principal == null) return ResponseEntity.status(401).build();

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.createAnalysis(userId, image, dto));
    }

    @GetMapping
    public ResponseEntity<List<SkinAnalysisResponseDto>> list(
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getAnalysisList(userId));
    }

    @GetMapping("/{analysisId}")
    public ResponseEntity<SkinAnalysisResponseDto> getAnalysis(
            @PathVariable UUID id,
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) return ResponseEntity.status(401).build();

        UUID userId = principal.getAttribute("userId");

        return ResponseEntity.ok(skinAnalysisService.getAnalysis(userId, id));
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

    @GetMapping("/{analysisId}/recommend")
    public void getAIRecommend(
            @PathVariable UUID analysisId) {

    }
}