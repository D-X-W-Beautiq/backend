package spring.beautiq.domain.skinanalysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spring.beautiq.domain.skinanalysis.dto.request.SkinAnalysisRequestDto;
import spring.beautiq.domain.skinanalysis.dto.response.SkinAnalysisResponseDto;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysis;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;
import spring.beautiq.domain.skinanalysis.repository.SkinAnalysisRepository;
import spring.beautiq.domain.user.entity.User;
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.exception.GlobalErrorCode;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {


    private final UserRepository userRepository;
    private final SkinAnalysisRepository skinAnalysisRepository;

    // todo: 프런트에서 이미지 받기 → AI 서버에 이미지 넘기기 → 분석 결과 받기 → 분석 결과 DB에 저장 및 프런트로 응답 반환하기
    public SkinAnalysisResponseDto createAnalysis(
            UUID userId,
            MultipartFile iamge,
            SkinAnalysisRequestDto dto
    ) {

        // 1. userId로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);

        // 2. MultipartFile을 AI 서버에 전송하여 분석 결과 받기
        // 3. 분석 결과를 DB에 저장
        // 4. 저장된 분석 결과를 프런트로 반환

        return null;
    }

    // todo: 분석 결과 리스트로 조회하기
    public List<SkinAnalysisResponseDto> getAnalysisList(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);

        List<SkinAnalysisResponseDto> skinAnalysis = skinAnalysisRepository.findAllByUserId(userId).stream()
                .map(SkinAnalysisResponseDto::from)
                .toList();

        return skinAnalysis;
    }

    // todo: 분석 결과 단건 조회하기
    @Transactional
    public SkinAnalysisResponseDto getAnalysis(UUID userId, UUID analysisId) {

        // 1. userId로 User 엔티티 조회
        User user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);
        // 2. analysisId로 분석 결과 단건 조회
        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);
        // 3. 분석 결과를 반환

        return SkinAnalysisResponseDto.from(skinAnalysis);
    }


    // todo : 분석 결과 삭제하기
    public void deleteAnalysis(UUID userId, UUID analysisId) {

        User user = userRepository.findById(userId)
                .orElseThrow(GlobalErrorCode.SECURITY_USER_NOT_FOUND::toException);

        SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(analysisId)
                .orElseThrow(SkinAnalysisExceptions.SKIN_ANALYSIS_NOT_FOUND::toException);

        if (!skinAnalysis.getUser().equals(user)) {
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
