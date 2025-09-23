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
import spring.beautiq.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 provider: " + registrationId);
        }


        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new CustomOAuth2User(userDTO);
        }

}
