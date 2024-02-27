package com.example.demo.JWT;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Jwt Token을 추출해 통과하면 권한을 부여하고 실패하면 권한을 부여하지 않고 다음 필터로 진행시킴
// OncePerRequestFilter: 매번 들어갈 때 마다 체크 해주는 필터
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // HTTP 헤더에서 'Authorization' 값을 가져옴
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Header의 Authorization 값이 비어있으면 => Jwt Token을 전송하지 않음
        if (authorizationHeader == null) {

            // 화면 로그인 시 쿠키의 "jwtToken"로 Jwt Token을 전송
            // 쿠키에도 Jwt Token이 없다면 로그인 하지 않은 것으로 간주
            if (request.getCookies() == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 쿠키에서 "jwtToken"을 Key로 가진 쿠키를 찾아서 가져오고 없으면 null return
            Cookie jwtTokenCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("jwtToken"))
                    .findFirst()
                    .orElse(null);

            if (jwtTokenCookie == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 쿠키 Jwt Token이 있다면 이 토큰으로 인증, 인가 진행
            String jwtToken = jwtTokenCookie.getValue();
            authorizationHeader = "Bearer " + jwtToken;
        }

        // Header의 Authorization 값이 'Bearer '로 시작하지 않으면 => 잘못된 토큰
        if (!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 전송받은 값에서 'Bearer ' 뒷부분(Jwt Token) 추출
        String token = authorizationHeader.split(" ")[1];

        // 전송받은 Jwt Token이 만료되었으면 => 다음 필터 진행(인증 X)
        if (JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

// Jwt Token에서 loginId 추출
        String loginId = JwtTokenUtil.getLoginId(token, secretKey);

// 추출한 loginId로 User 찾아오기
        User loginUser = userService.getLoginUserByLoginId(loginId);

        if (loginUser == null) {
            // OAuth2 토큰으로 인증된 사용자 확인
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) auth;
                String nickname = oauthToken.getName();

                // OAuth2 사용자 정보 처리 예시
                loginUser = userService.findByNickname(nickname);
            }
        }

// 여전히 loginUser가 null인 경우, 적절한 예외 처리가 필요할 수 있습니다.
        if(loginUser == null) {
            filterChain.doFilter(request, response);
            return;
        }

// loginUser 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser.getLoginId(), null, List.of(new SimpleGrantedAuthority(loginUser.getRole().name())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

// 권한 부여
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}