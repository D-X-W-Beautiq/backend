package spring.beautiq.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;

import spring.beautiq.global.exception.ApiException;
import spring.beautiq.global.exception.GlobalErrorCode;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder(
        @Value("${app.ai-server.url}") String aiServerUrl,
        @Value("${app.ai-server.connect-timeout-ms:3000}") int connectTimeoutMs,
        @Value("${app.ai-server.response-timeout-seconds:10}") long responseTimeoutSeconds,
        @Value("${app.ai-server.read-timeout-seconds:10}") int readTimeoutSeconds,
        @Value("${app.ai-server.write-timeout-seconds:10}") int writeTimeoutSeconds,
        @Value("${app.ai-server.max-in-memory-mb:20}") int maxInMemoryMb
    ) {
        // 인메모리 제한 (기본 20MB)
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(c -> c.defaultCodecs().maxInMemorySize(maxInMemoryMb * 1024 * 1024))
                .build();

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeoutSeconds))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeoutSeconds)));

        // 전역 상태코드 처리 + 네트워크/타임아웃 예외 매핑 필터
        ExchangeFilterFunction errorMappingFilter = (request, next) ->
                next.exchange(request)
                        .flatMap(response -> handleErrorStatus(response))
                        .onErrorMap(throwable -> mapNetworkErrors(throwable));

        return WebClient.builder()
                .baseUrl(aiServerUrl)
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(errorMappingFilter);
    }

    private Mono<ClientResponse> handleErrorStatus(ClientResponse response) {
        if (response.statusCode().isError()) {
            // 필요시 body 문자열을 로깅하거나 포함할 수 있으나, 여기서는 공용 예외로 변환만 수행
            return response
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono.error(new ApiException(GlobalErrorCode.INTERNAL_SERVER_ERROR)));
        }
        return Mono.just(response);
    }

    private Throwable mapNetworkErrors(Throwable t) {
        // Netty/Reactive에서 발생하는 대표적인 타임아웃 및 연결 끊김 예외를 공통 예외로 래핑
        if (t instanceof java.util.concurrent.TimeoutException
                || t instanceof io.netty.handler.timeout.ReadTimeoutException
                || t instanceof io.netty.channel.ConnectTimeoutException
                || t instanceof reactor.netty.http.client.PrematureCloseException) {
            return new ApiException(GlobalErrorCode.INTERNAL_SERVER_ERROR, t);
        }
        return t;
    }
}
