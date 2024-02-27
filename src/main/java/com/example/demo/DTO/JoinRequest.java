package com.example.demo.DTO;

import com.example.demo.user.UserRole;
import com.example.demo.user.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {
    private String username;
    private String loginId;
    private String password;
    private String passwordCheck;
    private String nickname;
    private String email;
    private String gender;
    private LocalDateTime createAt;
    private int age;


    // 비밀번호 암호화
    public User toEntity(String encodedPassword) {
        return User.builder()
                .username(this.username)
                .loginId(this.loginId)
                .password(encodedPassword)
                .nickname(this.nickname)
                .email(this.email)
                .role(UserRole.USER)
                .gender(this.gender)
                .age(this.age)
                .createAt(LocalDateTime.now())
                .build();
    }
}