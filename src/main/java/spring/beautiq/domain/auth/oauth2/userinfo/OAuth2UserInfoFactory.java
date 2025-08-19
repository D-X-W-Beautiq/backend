package spring.beautiq.domain.auth.oauth2.userinfo;

import java.util.Locale;
import java.util.Map;

public class OAuth2UserInfoFactory {
    private OAuth2UserInfoFactory() {}

    public static OAuth2UserInfo from(String registrationId, Map<String, Object> attributes) {
        String id = registrationId == null ? "" : registrationId.toLowerCase(Locale.ROOT);
        return switch (id) {
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new IllegalArgumentException("등록된 id를 찾을 수 없습니다. " + registrationId);
        };
    }
}
