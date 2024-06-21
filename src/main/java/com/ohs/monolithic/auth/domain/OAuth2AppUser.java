package com.ohs.monolithic.auth.domain;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

// 필드 수정 시, OAuth2AppUserMixin 수정 필요함
public class OAuth2AppUser extends DefaultOAuth2User implements AppUser {

    static GrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(UserRole.ADMIN.toString());

    @Setter
    private String nickname;

    @Setter
    @Getter
    private String provider;

    @Setter
    @Getter
    private String providerId;

    @Setter
    private Long accountId;

    public OAuth2AppUser(String provider, String providerId,  Long accountId, String nickname ,Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey) {
        super(authorities, attributes, nameAttributeKey);
        this.provider = provider;
        this.providerId = providerId;
        this.accountId = accountId;
        this.nickname = nickname;

    }


    @Override
    public Long getAccountId() {
        return accountId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public Boolean isAdmin() {
        return this.getAuthorities().contains(ADMIN_AUTHORITY);
    }




}