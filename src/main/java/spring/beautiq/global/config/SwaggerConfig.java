package spring.beautiq.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * - app.swagger.enabled=false 설정 시 비활성화됨
 * - 프로덕션 환경에서는 HTTPS URL로 자동 설정됨
 */
@Configuration
@ConditionalOnProperty(name = "app.swagger.enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerConfig {

    @Value("${spring.application.name:Beautiq}")
    private String applicationName;

    @Value("${app.swagger.server.url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.swagger.server.description:Development Server}")
    private String serverDescription;

    /**
     * OpenAPI 설정 Bean
     * - servers 설정으로 Swagger UI가 올바른 프로토콜(https/http)로 요청을 보냄
     * - ForwardedHeaderFilter와 함께 작동하여 프록시 환경에서도 정상 동작
     */
    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "BearerAuth";
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description(serverDescription)
                ))
                .components(new Components().addSecuritySchemes(
                        schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 토큰을 입력하세요 (Bearer 접두사 불필요)")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }

    private Info apiInfo() {
        return new Info()
                .title(applicationName + " API")
                .description("""
                        Beautiq 서비스 REST API 문서
                        
                        **주요 기능:**
                        - 피부 분석: AI 기반 피부 상태 분석 및 트렌드 조회
                        - 제품 추천: 피부 분석 결과 기반 맞춤 제품 추천
                        - 메이크업: AI 메이크업 시뮬레이션 및 커스터마이징 (Base64 기반)
                        - 사용자: OAuth2 로그인, 프로필 관리, 위시리스트
                        
                        **인증 방식:**
                        - JWT Bearer Token (쿠키 기반)
                        - OAuth2 (Google, Kakao)
                        
                        **HTTPS 환경:**
                        - 프로덕션 환경에서는 자동으로 HTTPS로 요청됩니다
                        - ForwardedHeaderFilter가 X-Forwarded-Proto 헤더를 처리합니다
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Beautiq Team")
                        .email("beautiq@example.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}