package com.example.demo.user;



import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "party")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(unique = true)
    private String nickname;

    private String email;
    //provider에는 "google"이 들어가게 되고, providerId에는 구글로 로그인한 유저의 고유 ID가 들어가게 됨
    private String provider;
    private String providerId;
    private String gender;
    private int age;
    private LocalDateTime createAt;

    private UserRole role;


    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    // ...

    public List<GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    public void edit(String newPassword, String newNickname, String newLoginId, String newUsername, int newAge) {
         this.loginId = newLoginId;
        this.username = newUsername;
        this.password = newPassword;
        this.nickname = newNickname;
        this.age = newAge;
    }
    public void apiEdit( String newNickname, String newLoginId, String newUsername, int newAge) {
        this.loginId = newLoginId;
        this.username = newUsername;
        this.nickname = newNickname;
        this.age = newAge;
    }

//    public void edit(String newPassword, String newNickname, String newLoginId, String newUsername, int newAge, int age, String password, String nickname,String loginId,String username) {
//        this.loginId = newLoginId;
//        this.username = newUsername;
//        this.password = newPassword;
//        this.nickname = newNickname;
//        this.age = newAge;
//    }


}