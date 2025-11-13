package spring.beautiq.domain.auth.oauth2.dto;


import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return (String) kakaoAccount.get("email");
        }
        return "kakao_" + attribute.get("id") + "@kakao.local";
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                return (String) profile.get("nickname");
            }
        }
        return null;
    }

    @Override
    public String getProfileImage() {
        return "";
    }
}