package com.example.demo;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final String loginId;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public UserDetailsImpl(String loginId, String password, List<GrantedAuthority> authorities) {
        this.loginId = loginId;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    // ...
}