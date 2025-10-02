package spring.beautiq.domain.skinanalysis.util;

import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse; // 새 Predictions 사용
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusType;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

public final class SkinAnalysisCalculator {
    private SkinAnalysisCalculator() {}

    // 종합 점수 계산 (Predictions 의 10개 정수 필드 평균)
    public static Float calcAverageScore(SkinAnalysisAIResponse.Predictions p) {
        if (p == null) return null;
        int sum = 0;
        int count = 0;
        if (p.getDryness() != null) { sum += p.getDryness(); count++; }
        if (p.getPigmentation() != null) { sum += p.getPigmentation(); count++; }
        if (p.getPore() != null) { sum += p.getPore(); count++; }
        if (p.getSagging() != null) { sum += p.getSagging(); count++; }
        if (p.getWrinkle() != null) { sum += p.getWrinkle(); count++; }
        if (p.getPigmentationReg() != null) { sum += p.getPigmentationReg(); count++; }
        if (p.getMoistureReg() != null) { sum += p.getMoistureReg(); count++; }
        if (p.getElasticityReg() != null) { sum += p.getElasticityReg(); count++; }
        if (p.getWrinkleReg() != null) { sum += p.getWrinkleReg(); count++; }
        if (p.getPoreReg() != null) { sum += p.getPoreReg(); count++; }
        if (count == 0) return null;
        return (float) sum / count; // 0~100 범위 내 평균
    }

    // averageScore(종합 점수) 기준으로 상태 분류
    public static SkinStatusType calcSkinStatus(SkinAnalysisEntity analysis) {
        if (analysis.getAverageScore() == null) return SkinStatusType.CAUTION;
        if (analysis.getAverageScore() < 30) return SkinStatusType.DANGER;
        if (analysis.getAverageScore() < 60) return SkinStatusType.CAUTION;
        return SkinStatusType.GOOD;
    }
}
