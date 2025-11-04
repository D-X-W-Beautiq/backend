package spring.beautiq.domain.skinanalysis.client;

import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;

public interface AIClient {
    SkinAnalysisAIResponse analyzeSkin(SkinAnalysisAIRequest request);
}

