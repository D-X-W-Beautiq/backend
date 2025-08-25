package spring.beautiq.domain.auth.oauth2.userinfo;

import java.util.Map;

public interface OAuth2UserInfo {
    String getName();
    String getEmail();
    String getProviderId();
    Map<String, Object> getAttributes();
}
