package spring.beautiq.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:Beautiq}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "BearerAuth";
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components().addSecuritySchemes(
                        schemeName,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
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