package spring.beautiq.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class SwaggerConfig {

    @Value("${app.openapi.title:Beautiq Swagger}")
    private String swaggerTitle;

    @Value("${app.openapi.description}")
    private String swaggerDescription;

    @Value("${app.openapi.version:${spring.application.version:0.0.1-SNAPSHOT}}")
    private String swaggerVersion;

    @Bean
    public OpenAPI openAPI() {
        final String schemeName = "BearerAuth";
        return new OpenAPI()
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
                .title(swaggerTitle)
                .description(swaggerDescription)
                .version(swaggerVersion);
    }
}