package spring.beautiq.domain.skinanalysis.util;

import spring.beautiq.domain.skinanalysis.dto.ai.common.SkinAnalysisAI;
import spring.beautiq.domain.skinanalysis.dto.common.SkinStatusType;
import spring.beautiq.domain.skinanalysis.entity.SkinAnalysisEntity;

public final class SkinAnalysisCalculator {
    private SkinAnalysisCalculator() {}

    // 종합 점수 계산 (피드백, id, createdAt 제외 모든 float 필드 평균)
    public static Float calcAverageScore(SkinAnalysisAI p) {
        float sum = 0f;
        int count = 0;
        if (p.getDryness() != null) { sum += p.getDryness(); count++; }
        if (p.getForeheadPigmentation() != null) { sum += p.getForeheadPigmentation(); count++; }
        if (p.getCheekPigmentation() != null) { sum += p.getCheekPigmentation(); count++; }
        if (p.getPore() != null) { sum += p.getPore(); count++; }
        if (p.getSagging() != null) { sum += p.getSagging(); count++; }
        if (p.getForeheadWrinkle() != null) { sum += p.getForeheadWrinkle(); count++; }
        if (p.getGlabellusWrinkle() != null) { sum += p.getGlabellusWrinkle(); count++; }
        if (p.getPerocularWrinkle() != null) { sum += p.getPerocularWrinkle(); count++; }
        if (p.getPigmentation() != null) { sum += p.getPigmentation(); count++; }
        if (p.getCheekPore() != null) { sum += p.getCheekPore(); count++; }
        if (p.getForeheadMoisture() != null) { sum += p.getForeheadMoisture(); count++; }
        if (p.getCheekMoisture() != null) { sum += p.getCheekMoisture(); count++; }
        if (p.getChinMoisture() != null) { sum += p.getChinMoisture(); count++; }
        if (p.getForeheadElasticityR2() != null) { sum += p.getForeheadElasticityR2(); count++; }
        if (p.getCheekElasticityR2() != null) { sum += p.getCheekElasticityR2(); count++; }
        if (p.getChinElasticityR2() != null) { sum += p.getChinElasticityR2(); count++; }
        if (p.getPerocularWrinkleRa() != null) { sum += p.getPerocularWrinkleRa(); count++; }
        if (count == 0) return null;
        return sum / count;
    }

    // averageScore(종합 점수) 기준으로 상태 분류
    public static SkinStatusType calcSkinStatus(SkinAnalysisEntity analysis) {
        if (analysis.getAverageScore() == null) return SkinStatusType.CAUTION;
        if (analysis.getAverageScore() < 30) return SkinStatusType.DANGER;
        if (analysis.getAverageScore() < 60) return SkinStatusType.CAUTION;
        return SkinStatusType.GOOD;
    }
}

