package spring.beautiq.domain.auth.oauth2.dto;

import java.io.Serializable;
import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return (String) attribute.get("email");
    }

    @Override
    public String getName() {
        return (String) attribute.get("name");
    }

    @Override
    public String getProfileImage() {
        return "";
    }
}
