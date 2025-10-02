package spring.beautiq.domain.auth.oauth2.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    // 단순히 기본 동작 유지 (Google OIDC는 SuccessHandler에서 처리)
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}
