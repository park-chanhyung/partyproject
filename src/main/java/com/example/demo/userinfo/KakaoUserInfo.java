package com.example.demo.userinfo;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String,Object> attributes;
    @Override
    public String getProviderId() {
        //카카오는 Long 타입 id임
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getEmail() {

        // kakao_account라는 Map에서 추출
        return (String) ((Map) attributes.get("kakao_account")).get("email");
    }
    @Override
    public String getName() {
        return (String) ((Map) attributes.get("properties")).get("nickname");
    }
}
