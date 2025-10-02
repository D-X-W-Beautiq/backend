package spring.beautiq.global.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * 인증된 사용자의 UUID를 SecurityContext(Authentication.principal)에서 추출하여
 * 컨트롤러 메서드 파라미터에 자동 주입하기 위한 어노테이션.
 * 클라이언트가 userId를 명시적으로 전송할 필요가 없으며 Swagger 문서에도 숨겨집니다.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Parameter(hidden = true)
public @interface CurrentUserId {
    // userId가 반드시 있어야 하는지 여부 (기본 true)
    boolean required() default true;
}
