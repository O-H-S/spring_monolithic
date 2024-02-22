package com.ohs.monolithic.user.service;

import com.ohs.monolithic.user.AppUserEntityMapper;
import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.LocalCredential;
import com.ohs.monolithic.user.domain.OAuth2Credential;
import com.ohs.monolithic.user.dto.OAuth2AppUser;
import com.ohs.monolithic.user.domain.UserRole;
import com.ohs.monolithic.user.repository.LocalCredentialRepository;
import com.ohs.monolithic.user.repository.OAuth2CredentialRepository;
import com.ohs.monolithic.utils.OAuth2ProviderIdExtractor;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.classgen.FinalVariableAnalyzer;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final Map<String, OAuth2ProviderIdExtractor> providerIdExtractors;
    private final OAuth2CredentialRepository oAuth2CredentialRepository;
    private final AppUserEntityMapper mapper;
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

        /*Debug 용
        oAuth2User.getAttributes().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
*/

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2ProviderIdExtractor extractor = providerIdExtractors.get(provider + "ProviderIdExtractor");
        String providerId = extractor != null ? extractor.extract(oAuth2User) : oAuth2User.getName();

        Account account = null;
        try {
            Optional<OAuth2Credential> credentialOp = oAuth2CredentialRepository.findByProviderAndProviderId(provider, providerId);

            if (credentialOp.isEmpty()) {
                long rand = (new Random()).nextLong();
                account = accountService.createAsOAuth2("사용자(" + rand + ")", "", provider, providerId);
            } else {
                account = credentialOp.get().getAccount();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
            throw e;
        }

        // 유저 권한 지정.
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(account.getRole().toString());


        // attribute 정의
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        //attributes.put("name", username);



        OAuth2AppUser appUser = new OAuth2AppUser(account, authorities, attributes,userNameAttributeName);

        mapper.map(appUser);
        return appUser;
    }
}
