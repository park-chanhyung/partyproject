package com.example.demo;

import com.example.demo.JWT.JwtTokenUtil;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserRole;
import com.example.demo.userinfo.GoogleUserInfo;
import com.example.demo.userinfo.KakaoUserInfo;
import com.example.demo.userinfo.NaverUserInfo;
import com.example.demo.userinfo.OAuth2UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Setter
@Getter
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil; // Add this line

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}",oAuth2User.getAttributes());
        OAuth2UserInfo oAuth2UserInfo = null;

        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("google")){
            log.info("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo( oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
            log.info("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo( (Map) oAuth2User.getAttributes() );
        } else if (provider.equals("naver")) {
            log.info("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo( (Map) oAuth2User.getAttributes().get("response") );

        }

        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String nickname = oAuth2UserInfo.getName();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        if (optionalUser.isEmpty()) {
            user = User.builder()
                    .loginId(loginId)
                    .nickname(oAuth2User.getAttribute("name"))
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        // 사용자 이름을 설정합니다.
        String name = oAuth2User.getAttribute("name");
        if (name == null || name.isEmpty()) {
            name = email;
        }

        // Issue JWT token here
        // Issue JWT token here
        String token = jwtTokenUtil.createToken(loginId, name, "secret-key", 60000L);

        // Return the token
        return new DefaultOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("name", name, "email", email, "token", token), // Add token to attributes
                "name"
        );
    }

}