package spring.beautiq.domain.skinanalysis.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import spring.beautiq.domain.skinanalysis.dto.ai.request.SkinAnalysisAIRequest;
import spring.beautiq.domain.skinanalysis.dto.ai.response.SkinAnalysisAIResponse;
import spring.beautiq.domain.skinanalysis.exception.SkinAnalysisExceptions;

@Component
@Profile("!mock-ai")
@RequiredArgsConstructor
public class RealAIClient implements AIClient {

    private final WebClient.Builder webClientBuilder;

    @Override
    public SkinAnalysisAIResponse analyzeSkin(SkinAnalysisAIRequest request) {
        SkinAnalysisAIResponse response = webClientBuilder.build()
                .post()
                .uri("/nia/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SkinAnalysisAIResponse.class)
                .block();

        if (response == null || response.getPredictions() == null) {
            throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
        }
        if (response.getFeedback() == null || response.getFeedback().isBlank()) {
            throw SkinAnalysisExceptions.AI_SERVER_RESPONSE_EMPTY.toException();
        }

        return response;
    }
}

