package spring.beautiq.global.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import spring.beautiq.global.security.resolver.CurrentUserIdArgumentResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserIdArgumentResolver currentUserIdArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedMethods = {"GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"};

        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://beautiq.my", "https://www.beautiq.my", "https://api.beautiq.my")  // 프론트 도메인
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdArgumentResolver);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                List<MediaType> types = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
                if (!types.contains(MediaType.APPLICATION_OCTET_STREAM)) {
                    types.add(MediaType.APPLICATION_OCTET_STREAM); // JSON 파트가 octet-stream으로 올 때 처리
                    jacksonConverter.setSupportedMediaTypes(types);
                }
            }
        }
    }
}