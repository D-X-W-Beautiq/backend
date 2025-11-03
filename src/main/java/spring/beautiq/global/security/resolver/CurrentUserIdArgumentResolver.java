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

        CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
        boolean required = annotation == null || annotation.required();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return handleMissingUser(required);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof OAuth2User oAuth2User)) {
            return handleMissingUser(required);
        }

        // 1차 시도: OAuth2User attributes에서 userId 추출
        UUID userId = extractUserIdFromAttributes(oAuth2User);
        if (userId != null) {
            return userId;
        }

        // 2차 시도: username으로 DB 조회
        userId = extractUserIdFromDatabase(oAuth2User);
        if (userId != null) {
            return userId;
        }

        return handleMissingUser(required);
    }

    private UUID extractUserIdFromAttributes(OAuth2User oAuth2User) {
        Object userIdAttr = oAuth2User.getAttribute("userId");

        if (userIdAttr == null) {
            return null;
        }

        String userIdStr = String.valueOf(userIdAttr);

        if (userIdStr.isBlank() || "null".equals(userIdStr)) {
            return null;
        }

        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw GlobalErrorCode.INVALID_ACCESS_TOKEN.toException();
        }
    }

    private UUID extractUserIdFromDatabase(OAuth2User oAuth2User) {
        Object usernameAttr = oAuth2User.getAttribute("username");

        if (usernameAttr == null) {
            return null;
        }

        String username = String.valueOf(usernameAttr);

        if (username.isBlank() || "null".equals(username)) {
            return null;
        }

        return userRepository.findByUsername(username)
                .map(BaseEntity::getId)
                .orElse(null);
    }

    private Object handleMissingUser(boolean required) {
        if (required) {
            throw GlobalErrorCode.SECURITY_USER_NOT_FOUND.toException();
        }
        return null;
    }
}
