package com.example.demo.DTO;

import com.example.demo.user.User;
import com.example.demo.user.UserRole;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRequest {
    private String username;
    private String loginId;
    private String password;
    private String nickname;
    private int age;
    private String nowPassword;
    private String newPassword;
    private String newPasswordCheck;
    private String gender;

    // 비밀번호 암호화
    public static UpdateRequest of(User user) {
        return UpdateRequest.builder()
                .username(user.getUsername())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .age(user.getAge())
                .gender(user.getGender())
                .build();
    }


}