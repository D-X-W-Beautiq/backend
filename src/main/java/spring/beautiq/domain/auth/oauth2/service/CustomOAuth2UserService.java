package spring.beautiq.domain.auth.oauth2.service;


import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.auth.oauth2.dto.GoogleResponse;
import spring.beautiq.domain.auth.oauth2.dto.KakaoResponse;
import spring.beautiq.domain.auth.oauth2.dto.OAuth2Response;
import spring.beautiq.domain.user.dto.UserDTO;
import spring.beautiq.domain.user.entity.UserEntity;
import spring.beautiq.domain.user.service.UserAuthService;
import spring.beautiq.global.exception.ApiException;
import spring.beautiq.global.exception.GlobalErrorCode;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserAuthService userAuthService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        try {
            OAuth2User rawUser = super.loadUser(userRequest);

            OAuth2Response oAuth2Response = switch (registrationId) {
                case "google" -> new GoogleResponse(rawUser.getAttributes());
                case "kakao" -> new KakaoResponse(rawUser.getAttributes());
                default -> throw GlobalErrorCode.OAUTH_UNSUPPORTED_PROVIDER.toException();
            };

            UserEntity user = userAuthService.upsert(
                    oAuth2Response.getProvider(),
                    oAuth2Response.getProviderId(),
                    oAuth2Response.getName(),
                    oAuth2Response.getEmail(),
                    "ROLE_USER"
            );

            return new CustomOAuth2User(UserDTO.from(user));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(GlobalErrorCode.OAUTH_USER_INFO_LOAD_FAILED, e);
        }
    }

}
