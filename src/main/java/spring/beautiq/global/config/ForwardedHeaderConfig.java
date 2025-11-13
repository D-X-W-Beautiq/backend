package spring.beautiq.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * 리버스 프록시(Nginx, ALB 등)가 전달하는 X-Forwarded-* 헤더를 처리하여
 * Spring이 실제 클라이언트의 프로토콜(https), 호스트, 포트를 올바르게 인식하도록 함.
 * 이를 통해 Swagger UI가 HTTPS로 API 요청을 보낼 수 있게 됨.
 */
@Configuration
public class ForwardedHeaderConfig {

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}

