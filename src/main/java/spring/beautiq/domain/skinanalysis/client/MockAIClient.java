package spring.beautiq.domain.skinanalysis.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;

import java.util.Random;

@Slf4j
@Component
@Profile("mock-ai")
public class MockAIClient implements AIClient {

    private final Random random = new Random();

    @Override
    public SkinAnalysisAIResponse analyzeSkin(SkinAnalysisAIRequest request) {
        log.info("MockAIClient 사용: 더미 데이터 반환");

        SkinAnalysisAIResponse.Predictions predictions = SkinAnalysisAIResponse.Predictions.builder()
                .dryness(randomScore())
                .pigmentation(randomScore())
                .pore(randomScore())
                .sagging(randomScore())
                .wrinkle(randomScore())
                .pigmentationReg(randomScore())
                .moistureReg(randomScore())
                .elasticityReg(randomScore())
                .wrinkleReg(randomScore())
                .poreReg(randomScore())
                .build();

        return SkinAnalysisAIResponse.builder()
                .status("success")
                .predictions(predictions)
                .feedback("테스트용 피드백: 전반적으로 양호한 피부 상태입니다. 꾸준한 관리를 권장합니다.")
                .build();
    }

    private Integer randomScore() {
        return 50 + random.nextInt(41); // 50~90
    }
}

