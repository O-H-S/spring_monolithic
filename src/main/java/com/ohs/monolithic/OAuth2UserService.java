package com.ohs.monolithic;

import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountRepository;
import com.ohs.monolithic.user.AccountService;
import com.ohs.monolithic.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final AccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Role generate
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(UserRole.USER.getValue());

        // nameAttributeKey
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        oAuth2User.getAttributes().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        // OAuth2 제공자 이름 가져오기 (예: "google", "facebook")
        String provider = userRequest.getClientRegistration().getRegistrationId();


        String username = oAuth2User.getAttribute("id").toString();
        Account _siteUser = null;
        try {
            _siteUser = this.accountService.getAccount(username);
        }
        catch(Exception e) {
            _siteUser = this.accountService.create(username,
                    null, "", provider, username);
        }


        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }
}
