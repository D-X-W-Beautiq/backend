package spring.beautiq.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title(swaggerTitle)
                .description(swaggerDescription)
                .version(swaggerVersion);
    }
}