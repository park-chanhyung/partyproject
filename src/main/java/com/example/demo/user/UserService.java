package com.example.demo.user;

import com.example.demo.DTO.GoogleRequest;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.JoinRequest;

import com.example.demo.DTO.UpdateRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * loginId 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }
    /**
     * nickname 중복 체크
     * 회원가입 기능 구현 시 사용
     * 중복되면 true return
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public BindingResult joinValid(JoinRequest req,BindingResult bindingResult)
    {

        if(req.getLoginId().isEmpty()){
            bindingResult.addError(new FieldError("req","loginId","아이디가 비었습니다."));
        }else if (req.getLoginId().length() > 3){
            bindingResult.addError(new FieldError("req","loginId","아이디가 10자가 넘습니다."));
        } else if (userRepository.existsByLoginId(req.getLoginId())) {
            bindingResult.addError(new FieldError("req","loginId","아이디가 중복됩니다."));
        }
        if(req.getPassword().isEmpty()){
            bindingResult.addError(new FieldError("req","password","비밀번호가 비어었습니다."));
        }
        if (!req.getPassword().equals(req.getPasswordCheck())) {
            bindingResult.addError(new FieldError("req", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }
        if (req.getNickname().isEmpty()) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 비어있습니다."));
        } else if (req.getNickname().length() > 10) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 10자가 넘습니다."));
        } else if (userRepository.existsByNickname(req.getNickname())) {
            bindingResult.addError(new FieldError("req", "nickname", "닉네임이 중복됩니다."));
        }
        if(userRepository.existsByEmail(req.getEmail())){
            bindingResult.addError(new FieldError("req","email","이메일이 중복됩니다."));
        }

        return bindingResult;
    }

    public BindingResult editValid(UpdateRequest updateRequest, BindingResult bindingResult, String loginId)
    {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (updateRequest.getNowPassword().isEmpty()) {
            bindingResult.addError(new FieldError("updateRequest", "nowPassword", "현재 비밀번호가 비어있습니다."));
        } else if (!encoder.matches(updateRequest.getNowPassword(), loginUser.getPassword())) {
            bindingResult.addError(new FieldError("updateRequest", "nowPassword", "현재 비밀번호가 틀렸습니다."));
        }
        if (!updateRequest.getNewPassword().equals(updateRequest.getNewPasswordCheck())) {
            bindingResult.addError(new FieldError("updateRequest", "newPasswordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (updateRequest.getNickname().isEmpty()) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 비어있습니다."));
        } else if (updateRequest.getNickname().length() > 10) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 10자가 넘습니다."));
        } else if (!updateRequest.getNickname().equals(loginUser.getNickname()) && userRepository.existsByNickname(updateRequest.getNickname())) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 중복됩니다."));
        }

        return bindingResult;
    }
    public BindingResult apiEditValid(UpdateRequest updateRequest, BindingResult bindingResult, String loginId)
    {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (updateRequest.getNickname().isEmpty()) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 비어있습니다."));
        } else if (updateRequest.getNickname().length() > 10) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 10자가 넘습니다."));
        } else if (!updateRequest.getNickname().equals(loginUser.getNickname()) && userRepository.existsByNickname(updateRequest.getNickname())) {
            bindingResult.addError(new FieldError("updateRequest", "nickname", "닉네임이 중복됩니다."));
        }

        return bindingResult;
    }
    /**
     * 회원가입 기능 2
     * 화면에서 JoinRequest(loginId, password, nickname)을 입력받아 User로 변환 후 저장
     * 회원가입 1과는 달리 비밀번호를 암호화해서 저장
     * loginId, nickname 중복 체크는 Controller에서 진행 => 에러 메세지 출력을 위해
     */
    public void join2(JoinRequest req) {
        userRepository.save(req.toEntity(encoder.encode(req.getPassword())));
    }

    public User myInfo(String loginId) {
        return userRepository.findByLoginId(loginId).get();
    }
    @Transactional
    public void edit(UpdateRequest updateRequest, String loginId) {
        User loginUser = userRepository.findByLoginId(loginId).get();

        if (updateRequest.getNewPassword().equals("")) {
            loginUser.edit(loginUser.getPassword(), updateRequest.getNickname(),updateRequest.getLoginId(),updateRequest.getAge(),updateRequest.getGender());
        } else {
            loginUser.edit(encoder.encode(updateRequest.getNewPassword()), updateRequest.getNickname(), updateRequest.getLoginId(), updateRequest.getAge(),updateRequest.getGender());
        }
    }
    @Transactional
    public void apiEdit(UpdateRequest updateRequest, String loginId) {
        User loginUser = userRepository.findByLoginId(loginId).get();

        loginUser.apiEdit(updateRequest.getNickname(),updateRequest.getUsername(),updateRequest.getAge(),updateRequest.getGender());

    }


    /**
     *  로그인 기능
     *  화면에서 LoginRequest(loginId, password)을 입력받아 loginId와 password가 일치하면 User return
     *  loginId가 존재하지 않거나 password가 일치하지 않으면 null return
     */
    public User login(LoginRequest req) {
        Optional<User> optionalUser = userRepository.findByLoginId(req.getLoginId());

        // loginId와 일치하는 User가 없으면 null return
        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // 찾아온 User의 password와 입력된 password가 다르면 null return
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return null; // 비밀번호가 일치하지 않는 경우
        }
//        if(!user.getPassword().equals(req.getPassword())) {
//            return null;
//        }

        return user;
    }

    public User googleLogin(GoogleRequest req) {
        // 구글 로그인에 사용되는 accessToken을 이용하여 사용자를 조회
        // 구글 로그인에서는 보통 이메일을 사용자의 식별자로 사용하므로, 이메일을 기반으로 사용자를 조회합니다.
        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        // 이메일과 일치하는 User가 없으면 null 반환
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // 조회한 User의 정보와 accessToken을 이용하여 구글 API를 통해 사용자의 이메일을 확인하고 일치하지 않으면 null 반환
        if (!verifyGoogleAccessToken(req.getAccessToken(), req.getEmail())) {
            return null; // 구글 로그인에 사용된 accessToken이 유효하지 않은 경우
        }

        return user;
    }

    private boolean verifyGoogleAccessToken(String accessToken, String email) {
        return true;
    }

    /**
     * userId(Long)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * userId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * userId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserById(Long userId) {
        if(userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    /**
     * loginId(String)를 입력받아 User을 return 해주는 기능
     * 인증, 인가 시 사용
     * loginId가 null이거나(로그인 X) userId로 찾아온 User가 없으면 null return
     * loginId로 찾아온 User가 존재하면 User return
     */
    public User getLoginUserByLoginId(String loginId) {
        if(loginId == null) return null;

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }
    public User getLoginUserByEmail(String email) {

        Optional<User> byEmail = userRepository.findByEmail(email);
        if(byEmail.isEmpty()) {
            Optional<User> byNickname = userRepository.findByNickname(email);
            log.info("이프 바이 닉네임"+byNickname);
            return byNickname.get();
        };
//        return byEmail.get();
        return byEmail.get();

    }

    public User findByNickname(String nickname) {
        // UserRepository를 사용하여 nickname으로 User 엔티티를 찾아옵니다.
        // 이때, User 엔티티가 없으면 null을 반환합니다.
        return userRepository.findByNickname(nickname).orElse(null);
    }
}