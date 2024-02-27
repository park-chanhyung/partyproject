package com.example.demo.JWT;


import com.example.demo.DTO.JoinRequest;
import com.example.demo.DTO.LoginRequest;

import com.example.demo.user.User;
import com.example.demo.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class JwtLoginApiController {

    private final UserService userService;

    @PostMapping("/join")
    public String join(@RequestBody JoinRequest joinRequest) {

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            return "로그인 아이디가 중복됩니다.";
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            return "닉네임이 중복됩니다.";
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return"바밀번호가 일치하지 않습니다.";
        }
        //이메일 중복체크
        if(userService.checkEmailDuplicate(joinRequest.getEmail())) {
            return "이메일이 중복됩니다.";
        }
        userService.join2(joinRequest);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {

        User user = userService.login(loginRequest);

        // 로그인 아이디나 비밀번호가 틀린 경우 global error return
        if(user == null) {
            return"로그인 아이디 또는 비밀번호가 틀렸습니다.";
        }

        // 로그인 성공 => Jwt Token 발급

        String secretKey = "my-secret-key-123123";
        long expireTimeMs = 1000 * 60 * 1;     // Token 유효 시간 = 10분

        String jwtToken = JwtTokenUtil.createToken(user.getLoginId(),user.getNickname(), secretKey, expireTimeMs);

        return jwtToken;
    }

    @GetMapping("/info")
    public String userInfo(Authentication auth) {
        User loginUser = userService.getLoginUserByLoginId(auth.getName());
        if(loginUser != null) {
            return String.format("loginId : %s\nnickname : %s\nrole : %s",
                    loginUser.getLoginId(), loginUser.getNickname(), loginUser.getRole().name());
        }
        return "/";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "관리자 페이지 접근 성공";
    }
}
