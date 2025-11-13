package spring.beautiq.domain.auth.oauth2.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("registrationId: {}", userRequest.getClientRegistration().getRegistrationId());
        log.info("accessToken: {}", userRequest.getAccessToken());
        log.info("additionalParameters: {}", userRequest.getAdditionalParameters());
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = createOAuth2Response(registrationId, oAuth2User);

        String providerId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();
        String profileImage = oAuth2Response.getProfileImage(); // 프로필 이미지 가져오기
        String name = oAuth2Response.getName();

        log.info("=== OAuth2 정보 ===");
        log.info("Provider: {}", oAuth2Response.getProvider());
        log.info("Email: {}", email);
        log.info("Name: {}", name);
        log.info("ProfileImage: {}", profileImage);


        // providerId로 먼저 조회 (이메일 변경 케이스 대응)
        UserEntity userEntity = userRepository.findByProviderId(providerId)
                .orElseGet(() -> {
                    // providerId 없으면 이메일로 조회
                    return userRepository.findByEmail(email)
                            .map(existing -> {
                                // 이메일로 찾았지만 providerId가 없는 경우 업데이트
                                existing.setProviderId(providerId);
                                existing.setProfileImage(profileImage); // 프로필 이미지 업데이트
                                return userRepository.save(existing);
                            })
                            .orElseGet(() -> {
                                // 완전 신규 사용자
                                UserEntity newUser = new UserEntity();
                                newUser.setProviderId(providerId);
                                newUser.setEmail(email);
                                newUser.setRole("ROLE_USER");
                                newUser.setProfileImage(profileImage); // 프로필 이미지 저장
                                return userRepository.save(newUser);
                            });
                });

        // providerId에서 provider 추출 (kakao_123456 -> kakao)
        String provider = null;
        if (providerId != null && providerId.contains("_")) {
            provider = providerId.split("_")[0];
        }

        OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                .userId(userEntity.getId().toString())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .email(userEntity.getEmail())              // 추가
                .profileImage(userEntity.getProfileImage()) // 추가
                .provider(provider)                         // 추가
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