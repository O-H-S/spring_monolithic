package com.ohs.monolithic.user.service;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.LocalCredential;
import com.ohs.monolithic.user.domain.OAuth2Credential;
import com.ohs.monolithic.user.dto.OAuth2AppUser;
import com.ohs.monolithic.user.domain.UserRole;
import com.ohs.monolithic.user.repository.LocalCredentialRepository;
import com.ohs.monolithic.user.repository.OAuth2CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.classgen.FinalVariableAnalyzer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OAuth2AccountService extends DefaultOAuth2UserService {
    private final OAuth2CredentialRepository oAuth2CredentialRepository;
    private final AccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // super.loadUser 이 메소드의 반환 타입은 DefaultOAuth2User 이다.
        OAuth2User oAuth2User = super.loadUser(userRequest);



        // 유저를 식별할 수 있는 attributeKey를 가져온다.
        // 이 값은 사전에 application 설정 파일 안에 정의된 상태이다. (user-name-attribute)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        //System.out.println((userNameAttributeName));  카카오, "id" 출력.

        // Debug 용
        oAuth2User.getAttributes().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        // OAuth2 제공자 이름 가져오기 (예: "google", "facebook")
        String provider = userRequest.getClientRegistration().getRegistrationId();


        // 고유한 id 값을 name으로 가지는 계정이 없다면, 새롭게 만든다.
        //
        String providerId = Objects.requireNonNull(oAuth2User.getAttribute(userNameAttributeName)).toString();

        Optional<OAuth2Credential> credentialOp = oAuth2CredentialRepository.findByProviderAndProviderId(provider, providerId);
        Account account = null;
        if (credentialOp.isEmpty()) {
            account = accountService.createAsOAuth2("", "", provider, providerId);
        }
        else{
            account = credentialOp.get().getAccount();
        }

        // 유저 권한 지정.
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(account.getRole().toString());


        // attribute 정의
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        //attributes.put("name", username);



        return new OAuth2AppUser(account, authorities, attributes,userNameAttributeName);
    }
}
