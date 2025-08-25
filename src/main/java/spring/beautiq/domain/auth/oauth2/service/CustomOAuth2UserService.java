package spring.beautiq.domain.auth.oauth2.service;


import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.beautiq.domain.auth.oauth2.userinfo.OAuth2UserInfo;
import spring.beautiq.domain.auth.oauth2.userinfo.OAuth2UserInfoFactory;
import spring.beautiq.domain.user.AuthProvider.AuthProvider;
import spring.beautiq.domain.user.entity.User;
import spring.beautiq.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User delegate = super.loadUser(req);
        String registrationId = req.getClientRegistration().getRegistrationId();


        // 1. 표준화
        OAuth2UserInfo info = OAuth2UserInfoFactory.from(registrationId, delegate.getAttributes());
        AuthProvider provider = switch (registrationId.toLowerCase(Locale.ROOT)) {
            case "google" -> AuthProvider.GOOGLE;
            case "kakao"  -> AuthProvider.KAKAO;
            default -> throw new IllegalArgumentException("지원되지 않는 사용자입니다.: " + registrationId);
        };

        // 2. 가입,수정
        User user = userRepository.findByProviderAndProviderId(provider, info.getProviderId())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .provider(provider)
                                .providerId(info.getProviderId())
                                .email(info.getEmail())
                                .name(info.getName())
                                .roles(Set.of("ROLE_USER"))
                                .build()
                ));

        boolean changed = false;
        if (!Objects.equals(user.getEmail(), info.getEmail())) { user.setEmail(info.getEmail()); changed = true; }
        if (!Objects.equals(user.getName(),  info.getName()))  { user.setName(info.getName());  changed = true; }
        if (changed) userRepository.save(user);


        Map<String, Object> attrs = new HashMap<>();
        attrs.put("provider", provider.name());
        attrs.put("providerId", info.getProviderId());
        attrs.put("userId", user.getId());
        attrs.put("email", user.getEmail());
        attrs.put("name", user.getName());

        return new DefaultOAuth2User(Collections.emptyList(), attrs, "providerId");
    }
}