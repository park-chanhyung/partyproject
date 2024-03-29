package com.example.demo.JWT;


import com.example.demo.DTO.JoinRequest;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.UpdateRequest;
import com.example.demo.PrincipalOauth2UserService;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class JwtLoginController {
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    @GetMapping(value = {"", "/"})
    public String home(Model model, Authentication auth) {
        // 인증정보가 있으면 닉네임을 모델에 추가
        if(auth != null) {
            User loginUser = userService.getLoginUserByLoginId(auth.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }

        return "index";
    }
    //회원가입 페이지
    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }
//회원가입 처리
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "이미 가입된 ID입니다."));
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "이미 가입된 닉네임입니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        }
        if(userService.checkEmailDuplicate(joinRequest.getEmail())) {
            bindingResult.addError(new FieldError("joinRequest", "email", "이미 가입된 이메일입니다."));
        }
        // 에러가 있으면 회원가입 페이지로 돌아감
        if(bindingResult.hasErrors()) {
            return "join";
        }
    // 회원가입 성공하면 홈페이지로
        userService.join2(joinRequest);
        return "redirect:/";
    }

    //로그인페이지
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "jwt-login");
        model.addAttribute("pageName", "Jwt Token 화면 로그인");
        model.addAttribute("loginRequest", new LoginRequest());
       log.info("로그인 컨트롤러");
        return "login";
    }
    // 로그인 처리
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
                        HttpServletResponse response, Model model) {
        model.addAttribute("loginType", "jwt-login");
        model.addAttribute("pageName", "Jwt Token 화면 로그인");
        System.out.println("포스트로그인 컨트롤러");
        User user = userService.login(loginRequest);

        // 로그인 아이디나 비밀번호가 틀린 경우 global error return
        if(user == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if(bindingResult.hasErrors()) {
            return "login";
        }

        // 로그인 성공 => Jwt Token 발급
        String secretKey = "my-secret-key-123123";
        long expireTimeMs = 1000 * 60 * 1;     // Token 유효 시간 = 10분

        model.addAttribute("jwtTime",expireTimeMs);
        log.info("jwt 타임" + expireTimeMs);
        log.info("로그인확인");
        String jwtToken = JwtTokenUtil.createToken(user.getLoginId(),user.getNickname(), secretKey, expireTimeMs);

        // 발급한 Jwt Token을 Cookie를 통해 전송
        // 클라이언트는 다음 요청부터 Jwt Token이 담긴 쿠키 전송 => 이 값을 통해 인증, 인가 진행
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setMaxAge(60 * 1);  // 쿠키 유효 시간 : 10분
        response.addCookie(cookie);

        return "redirect:/";
    }
    @GetMapping("/apiLogin")
    public String apiLogin(@AuthenticationPrincipal OAuth2User principal,
                              HttpServletResponse response,
                              Model model) {
        // OAuth2 로그인을 통해 가져온 사용자 정보
        String loginId = principal.getAttribute("email");
        String nickname = principal.getAttribute("name");

        // JWT 토큰 생성을 위한 설정
        String secretKey = "my-secret-key-123123";
        long expireTimeMs = 1000 * 60 * 1;     // Token 유효 시간 = 1분

        model.addAttribute("jwtTime",expireTimeMs);

        // JWT 토큰 생성
        String jwtToken = JwtTokenUtil.createToken(loginId, nickname, secretKey, expireTimeMs);

        // 발급한 Jwt Token을 Cookie를 통해 전송
        // 클라이언트는 다음 요청부터 Jwt Token이 담긴 쿠키 전송 => 이 값을 통해 인증, 인가 진행
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setMaxAge(60 * 1);  // 쿠키 유효 시간 : 1분
        response.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/jwtLogout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        // 쿠키 파기
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
    // 내정보
    @GetMapping("/info")
    public String userInfo(Model model, Authentication auth) {
        User loginUser = userService.getLoginUserByLoginId(auth.getName());
            log.info("유저정보"+loginUser);
        if (loginUser == null) {
            User Email = userService.getLoginUserByEmail(auth.getName());
            model.addAttribute("user",Email);
            log.info("이프 유저정보"+auth.getName());
            log.info("이프 유저정보"+Email);
        } else  {
            model.addAttribute("user", loginUser);
            log.info("엘쓰 유저정보"+loginUser);
        }

        return "info";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/update")
    public String update(Authentication auth , Model model) {
        User user = this.userService.myInfo(auth.getName());
        log.info("수정할유저"+user);
//        model.addAttribute("updateRequest", new UpdateRequest());
        model.addAttribute("user",UpdateRequest.of(user));

        return "update";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update")
    public String update(@Valid @ModelAttribute UpdateRequest updateRequest, BindingResult bindingResult,
                           Authentication auth) {
        // Validation
        if (userService.editValid(updateRequest, bindingResult, auth.getName()).hasErrors()) {
            return "/update";
        }
        userService.edit(updateRequest,auth.getName());
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/apiUpdate")
    public String apiUpdate(Authentication auth , Model model) {
        User user = this.userService.myInfo(auth.getName());
        log.info("수정할유저"+user);
//        model.addAttribute("updateRequest", new UpdateRequest());
        model.addAttribute("user",UpdateRequest.of(user));

        return "apiUpdate";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/apiUpdate")
    public String apiUpdate(@Valid @ModelAttribute UpdateRequest updateRequest, BindingResult bindingResult,
                            Authentication auth, Model model) {
        // Validation
        if (bindingResult.hasErrors()) {
            // 유효성 검사에 실패하면 다시 "/apiUpdate" 뷰를 보여줍니다.
            return "apiUpdate";
        }
        userService.apiEdit(updateRequest, auth.getName());
        model.addAttribute("message", "정보 수정이 완료되었습니다!");
        model.addAttribute("nextUrl", "/");
        return "redirect:/";
    }



    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("loginType", "jwt-login");
        model.addAttribute("pageName", "Jwt Token 화면 로그인");

        return "admin";
    }
// 인증실패 페이지
    @GetMapping("/authentication-fail")
    public String authenticationFail(Model model) {
        model.addAttribute("loginType", "jwt-login");
        model.addAttribute("pageName", "Jwt Token 화면 로그인");

        return "errorPage/authenticationFail";
    }
// 인가실패 페이지
    @GetMapping("/authorization-fail")
    public String authorizationFail(Model model) {
        model.addAttribute("loginType", "jwt-login");
        model.addAttribute("pageName", "Jwt Token 화면 로그인");

        return "errorPage/authorizationFail";
    }

}
