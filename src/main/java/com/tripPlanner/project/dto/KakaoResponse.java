package com.tripPlanner.project.dto;

import java.util.Map;


/*{
        "id": 123456789, // 카카오 사용자 ID
        "kakao_account": {
        "profile": {
        "nickname": "사용자 이름",
        "thumbnail_image_url": "썸네일 이미지 URL",
        "profile_image_url": "프로필 이미지 URL",
        "is_default_image": true  // 기본 이미지 여부
        },
        "email": "사용자 이메일",
        "age_range": "나이대 정보",
        "birthday": "생일",
        "gender": "성별"
        }
   }*/
public class KakaoResponse implements OAuth2Response {

    private final String id;
    private final String email;
    private final String nickname;

    public KakaoResponse(Map<String, Object> attributes) {
        this.id = attributes.get("id").toString();

        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        this.email = (String) account.get("email");

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        this.nickname = (String) profile.get("nickname");
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return nickname;
    }
}
