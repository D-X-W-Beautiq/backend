package spring.beautiq.global.security.resolver;

import lombok.RequiredArgsConstructor;
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
import spring.beautiq.domain.user.repository.UserRepository;
import spring.beautiq.global.base.BaseEntity;
import spring.beautiq.global.exception.GlobalErrorCode;
import spring.beautiq.global.security.annotation.CurrentUserId;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

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

        // userId 추출 시도
        return extractUserId(oAuth2User, required);
    }

    private UUID extractUserId(OAuth2User oAuth2User, boolean required) {
        // 1차: attributes에서 직접 추출
        Object userIdAttr = oAuth2User.getAttribute("userId");
        if (userIdAttr != null) {
            try {
                return UUID.fromString(String.valueOf(userIdAttr));
            } catch (IllegalArgumentException e) {
                if (required) {
                    throw GlobalErrorCode.INVALID_ACCESS_TOKEN.toException();
                }
            }
        }

        // 2차: username으로 DB 조회 (JWT에 username이 포함되므로)
        Object usernameAttr = oAuth2User.getAttribute("username");
        if (usernameAttr != null) {
            String username = String.valueOf(usernameAttr);
            if (!username.isBlank() && !"null".equals(username)) {
                return userRepository.findByUsername(username)
                        .map(BaseEntity::getId)
                        .orElseGet(() -> handleMissingUser(required));
            }
        }

        return handleMissingUser(required);
    }

    private UUID handleMissingAuthentication(boolean required) {
        if (required) {
            throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
        }
        return null;
    }

    private UUID handleMissingUser(boolean required) {
        if (required) {
            throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
        }
        return null;
    }
}
