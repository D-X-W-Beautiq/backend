package spring.beautiq.domain.auth.oauth2.userinfo;


import java.util.Collections;
import java.util.Map;

@SuppressWarnings("unchecked")
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> account;
    private final Map<String, Object> profile;

    public KakaoUserInfo(Map<String, Object> attributes) {

        this.attributes = attributes;
        this.account = (Map<String, Object>) attributes.getOrDefault("kakao_account", Collections.emptyMap());
        this.profile = (Map<String, Object>) account.getOrDefault("profile", Collections.emptyMap());
    }

    @Override
    public String getProviderId() {

        Object id = attributes.get("id");
        return id == null ? null : String.valueOf(id);
    }

    @Override
    public String getEmail() {

        Object email = account.get("email");
        return email == null ? null : email.toString();
    }

    @Override
    public String getName() {

        Object nickname = profile.get("nickname");
        return nickname == null ? getEmail() : nickname.toString();
    }

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

}