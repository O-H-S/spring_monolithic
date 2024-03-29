package com.ohs.monolithic.user.dto;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.dto.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class OAuth2AppUser extends DefaultOAuth2User implements AppUser {

    final private Account account;

    public OAuth2AppUser(Account account, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
        this.account = account;
    }


    @Override
    public Long getAccountId() {
        return account.getId();
    }
    @Override
    public Account getAccount() {
        return account;
    }


}