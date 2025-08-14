package spring.beautiq.domain.auth.oauth2.userinfo;

import java.util.Map;


public class GoogleUserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
