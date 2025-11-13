package spring.beautiq.domain.auth.oauth2.dto;


import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }


    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");

        System.out.println("=== 카카오 전체 데이터 ===");
        System.out.println(attribute);
        System.out.println("=== kakao_account ===");
        System.out.println(kakaoAccount);

        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return (String) kakaoAccount.get("email");
        }
        System.out.println("❌ 이메일 없음! 기본값 사용");
        return "kakao_" + attribute.get("id") + "@kakao.local";
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");


            System.out.println("=== profile ===");
            System.out.println(profile);

            if (profile != null) {
                String nickname = (String) profile.get("nickname");
                System.out.println("✅ 닉네임 받음: " + nickname);
                return (String) profile.get("nickname");
            }
        }

        System.out.println("❌ 닉네임 없음!");
        return null;
    }

    @Override
    public String getProfileImage() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null && profile.containsKey("profile_image_url")) {
                return (String) profile.get("profile_image_url");
            }
        }
        return null;
    }
}