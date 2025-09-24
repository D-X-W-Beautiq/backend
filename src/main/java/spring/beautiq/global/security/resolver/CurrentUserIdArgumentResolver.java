package spring.beautiq.global.security.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.global.security.annotation.CurrentUserId;

import java.util.UUID;

public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    // currentUserId 어노테이션이 붙어있고, 파라미터 타입이 UUID인 경우 이 리졸버가 동작
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && UUID.class.isAssignableFrom(parameter.getParameterType());
    }

    // 실제로 파라미터를 어떻게 처리할지 정의
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {
        CurrentUserId ann = parameter.getParameterAnnotation(CurrentUserId.class);
        boolean required = ann == null || ann.required();

        // SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            if (required) {
                throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
            }
            return null;
        }

        Object principal = authentication.getPrincipal();
        String userIdStr = null;

        if (principal instanceof OAuth2User oAuth2User) {
            Object attr = oAuth2User.getAttribute("userId");
            if (attr != null) {
                userIdStr = String.valueOf(attr);
            }
        }

        // userId가 없거나 빈 문자열인 경우 처리
        if (userIdStr == null || userIdStr.isBlank()) {
            if (required) {
                throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
            }
            return null;
        }

        // userId 문자열을 UUID로 변환, 변환 실패 시 예외 처리
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw GlobalErrorCode.INVALID_ACCESS_TOKEN.toException();
        }
    }
}
