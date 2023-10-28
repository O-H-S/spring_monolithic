package com.ohs.monolithic.user.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {


    //
    final private OAuth2User oAuth2User;

    public CustomOAuth2User(OAuth2User oAuth2User) {
        this.oAuth2User = oAuth2User;
    }

    // ---------------------------- OAuth2User 메서드 정의------------------------
    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    // Collection<? extends GrantedAuthority> 이건 뭐지? (공부하기)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }

    // ---------------------UserDetails 메서드 구현---------------------------------

    //Aouth2 로그인 이용자는 비밀번호가 없다
    @Override
    public String getPassword() {
        return null;
    }

    // name attribute는 사전에 추가되어야한다.
    @Override
    public String getUsername() {
        return oAuth2User.getAttribute("name"); //
    }

    // 나머지 공부하여 구현해야함.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}