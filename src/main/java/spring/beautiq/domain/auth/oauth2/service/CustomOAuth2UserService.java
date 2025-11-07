package spring.beautiq.domain.auth.oauth2.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spring.beautiq.domain.auth.oauth2.dto.CustomOAuth2User;
import spring.beautiq.domain.auth.oauth2.dto.GoogleResponse;
import spring.beautiq.domain.auth.oauth2.dto.KakaoResponse;
import spring.beautiq.domain.auth.oauth2.dto.OAuth2Response;
import spring.beautiq.domain.user.dto.OAuth2UserDTO;
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
        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User);

        String providerId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();

        // providerId로 먼저 조회 (이메일 변경 케이스 대응)
        UserEntity userEntity = userRepository.findByProviderId(providerId)
                .orElseGet(() -> {
                    // providerId 없으면 이메일로 조회
                    return userRepository.findByEmail(email)
                            .map(existing -> {
                                // 이메일로 찾았지만 providerId가 없는 경우 업데이트
                                existing.setProviderId(providerId);
                                return userRepository.save(existing);
                            })
                            .orElseGet(() -> {
                                // 완전 신규 사용자
                                UserEntity newUser = new UserEntity();
                                newUser.setProviderId(providerId);
                                newUser.setEmail(email);
                                newUser.setUsername(generateDefaultUsername(email));
                                newUser.setRole("ROLE_USER");
                                return userRepository.save(newUser);
                            });
                });

        OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                .userId(userEntity.getId().toString())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();

        return new CustomOAuth2User(userDTO);
    }

    private String generateDefaultUsername(String email) {
        // 이메일 @ 앞부분을 기본 닉네임으로 사용
        String baseUsername = email.split("@")[0];

        // 중복 체크 후 숫자 suffix 추가
        String username = baseUsername;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
        }
        return username;
    }



    private OAuth2Response createOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        if (registrationId.equals("google")) {
            return new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {
            return new KakaoResponse(oAuth2User.getAttributes());
        }
        throw new OAuth2AuthenticationException(
                new OAuth2Error("unsupported_provider"),
                "지원하지 않는 provider: " + registrationId);
    }
}
