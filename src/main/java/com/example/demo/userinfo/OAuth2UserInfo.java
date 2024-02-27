package com.example.demo.userinfo;

public interface OAuth2UserInfo {
    //사이트별로 값을 추출하는 방식은 다르지만 추출해야 하는 값은 같기 때문에 통일성을 위해 interface 생성
    //생성한 interface를 상속받아 사용
    String getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
