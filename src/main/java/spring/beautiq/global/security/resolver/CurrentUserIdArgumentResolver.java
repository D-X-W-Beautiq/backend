package spring.beautiq.global.security.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.global.security.annotation.CurrentUserId;

import java.util.UUID;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && UUID.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {

        CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
        boolean required = annotation == null || annotation.required();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return handleMissingAuthentication(required);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oAuth2User)) {
            return handleMissingAuthentication(required);
        }

        // userId 추출 시도 (엄격 모드: 반드시 존재하고 UUID 형식이어야 함)
        return extractUserId(oAuth2User, required);
    }

    private UUID extractUserId(OAuth2User oAuth2User, boolean required) {
        Object userIdAttr = oAuth2User.getAttribute("userId");
        if (userIdAttr == null) {
            if (required) {
                throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
            }
            return null;
        }

        try {
            return UUID.fromString(String.valueOf(userIdAttr));
        } catch (IllegalArgumentException e) {
            // userId가 UUID 형식이 아닌 경우 에러
            throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
        }
    }

    private UUID handleMissingAuthentication(boolean required) {
        if (required) {
            throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
        }
        return null;
    }

}
