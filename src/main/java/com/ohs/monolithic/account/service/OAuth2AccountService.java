package com.ohs.monolithic.account.service;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.OAuth2Credential;
import com.ohs.monolithic.account.event.AccountDataChangeEvent;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.auth.domain.OAuth2AppUser;
import com.ohs.monolithic.account.repository.OAuth2CredentialRepository;
import com.ohs.monolithic.auth.service.AppUserEntityMapper;
import com.ohs.monolithic.common.utils.OAuth2ProviderIdExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OAuth2AccountService extends DefaultOAuth2UserService {
    private final Map<String, OAuth2ProviderIdExtractor> providerIdExtractors;
    private final OAuth2CredentialRepository oAuth2CredentialRepository;
    private final AccountService accountService;
    private final ClientRegistrationRepository clientRegistrationRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // super.loadUser 이 메소드의 반환 타입은 DefaultOAuth2User 이다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 유저를 식별할 수 있는 attributeKey를 가져온다.
        // 이 값은 사전에 application 설정 파일 안에 정의된 상태이다. (user-name-attribute)
        /*String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();*/


        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2ProviderIdExtractor extractor = providerIdExtractors.get(provider + "ProviderIdExtractor");
        String providerId = extractor != null ? extractor.extract(oAuth2User) : oAuth2User.getName();

        Account account = loadFromDB(provider, providerId);

        OAuth2AppUser result = wrapPrincipal(oAuth2User, provider, providerId, account);

        return result;
    }
    Account loadFromDB(String provider, String providerId){
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
        return account;
    }


    OAuth2AppUser wrapPrincipal(OAuth2User source, String sourceProvider, String sourceProviderId,Account account){
        String userNameAttributeName = clientRegistrationRepository.findByRegistrationId(sourceProvider).getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();


        Set<GrantedAuthority> authorities = new HashSet<>(source.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(account.getRole().toString()));

        Map<String, Object> attributes = new HashMap<>(source.getAttributes());

        OAuth2AppUser appUser = new OAuth2AppUser(sourceProvider,sourceProviderId, account.getId(), account.getNickname(), authorities, attributes, userNameAttributeName);

        return appUser;
    }


    //@Transactional( readOnly = true, propagation = Propagation.REQUIRES_NEW)
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void reload(AccountDataChangeEvent event){
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof OAuth2AuthenticationToken) {
            OAuth2AppUser oldPrincipal = (OAuth2AppUser) currentAuth.getPrincipal();
            OAuth2AppUser newPrincipal = wrapPrincipal(oldPrincipal, oldPrincipal.getProvider(), oldPrincipal.getProviderId(),  loadFromDB(oldPrincipal.getProvider(), oldPrincipal.getProviderId()));

            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(newPrincipal, currentAuth.getAuthorities(), ((OAuth2AuthenticationToken) currentAuth).getAuthorizedClientRegistrationId());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}
