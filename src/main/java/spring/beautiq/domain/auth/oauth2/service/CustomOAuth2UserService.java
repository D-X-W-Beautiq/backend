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
        String profileImage = oAuth2Response.getProfileImage();
        String name = oAuth2Response.getName();

        log.info("=== OAuth2 정보 ===");
        log.info("Provider: {}", oAuth2Response.getProvider());
        log.info("Email: {}", email);
        log.info("Name: {}", name);
        log.info("ProfileImage: {}", profileImage);

        log.info("=== 사용자 조회 시작 ===");
        log.info("ProviderId: {}", providerId);

        // providerId로 먼저 조회 (이메일 변경 케이스 대응)
        UserEntity userEntity = userRepository.findByProviderId(providerId)
                .orElseGet(() -> {
                    log.info("providerId로 사용자 없음, 이메일로 조회: {}", email);

                    // providerId 없으면 이메일로 조회
                    return userRepository.findByEmail(email)
                            .map(existing -> {
                                log.info("이메일로 기존 사용자 발견, 업데이트");
                                // 이메일로 찾았지만 providerId가 없는 경우 업데이트
                                existing.setProviderId(providerId);
                                existing.setProfileImage(profileImage);
                                // username이 없으면 설정
                                if (existing.getUsername() == null || existing.getUsername().isEmpty()) {
                                    existing.setUsername(generateUniqueUsername(name, email));
                                }
                                UserEntity saved = userRepository.save(existing);
                                log.info("기존 사용자 업데이트 완료 - ID: {}", saved.getId());
                                return saved;
                            })
                            .orElseGet(() -> {
                                log.info("=== 완전 신규 사용자 생성 시작 ===");
                                try {
                                    // 완전 신규 사용자
                                    UserEntity newUser = new UserEntity();
                                    log.info("1. UserEntity 객체 생성 완료");

                                    newUser.setProviderId(providerId);
                                    log.info("2. ProviderId 설정 완료: {}", providerId);

                                    newUser.setEmail(email);
                                    log.info("3. Email 설정 완료: {}", email);

                                    String username = generateUniqueUsername(name, email);
                                    log.info("4. Username 생성 완료: {}", username);

                                    newUser.setUsername(username);
                                    log.info("5. Username 설정 완료");

                                    newUser.setRole("ROLE_USER");
                                    log.info("6. Role 설정 완료");

                                    newUser.setProfileImage(profileImage);
                                    log.info("7. ProfileImage 설정 완료");

                                    log.info("8. DB 저장 시작...");
                                    UserEntity saved = userRepository.save(newUser);
                                    log.info("9. ✅ DB 저장 완료 - ID: {}", saved.getId());

                                    return saved;
                                } catch (Exception e) {
                                    log.error("❌ 신규 사용자 생성 실패!", e);
                                    throw e;
                                }
                            });
                });

        log.info("=== 최종 사용자 정보 ===");
        log.info("User ID: {}", userEntity.getId());
        log.info("Username: {}", userEntity.getUsername());
        log.info("Email: {}", userEntity.getEmail());

        // providerId에서 provider 추출 (kakao_123456 -> kakao)
        String provider = null;
        if (providerId != null && providerId.contains("_")) {
            provider = providerId.split("_")[0];
        }

        OAuth2UserDTO userDTO = OAuth2UserDTO.builder()
                .userId(userEntity.getId().toString())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .email(userEntity.getEmail())
                .profileImage(userEntity.getProfileImage())
                .provider(provider)
                .build();

        return new CustomOAuth2User(userDTO);
    }

    private String generateUniqueUsername(String name, String email) {
        // 1. name이 있으면 name 사용, 없으면 email 앞부분 사용
        String baseUsername = (name != null && !name.isEmpty())
                ? name
                : email.split("@")[0];

        log.info("baseUsername: {}", baseUsername);

        // 2. 중복 체크
        String username = baseUsername;
        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + suffix;
            suffix++;
            log.info("중복 발견, 새로운 username 시도: {}", username);
        }

        log.info("최종 username: {}", username);
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