package com.ohs.monolithic.user.oauth2;

import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import com.ohs.monolithic.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
    private final AccountService accountService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // super.loadUser 이 메소드의 반환 타입은 DefaultOAuth2User 이다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 유저 권한 지정.
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(UserRole.USER.getValue());

        // 유저를 식별할 수 있는 attributeKey를 가져온다.
        // 이 값은 사전에 application 설정 파일 안에 정의된 상태이다. (user-name-attribute)
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        //System.out.println((userNameAttributeName)); 는 "id"라는 문자열을 반환한다.

        // Debug 용
        oAuth2User.getAttributes().forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        // OAuth2 제공자 이름 가져오기 (예: "google", "facebook")
        String provider = userRequest.getClientRegistration().getRegistrationId();


        // 고유한 id 값을 name으로 가지는 계정이 없다면, 새롭게 만든다.
        //
        String username = Objects.requireNonNull(oAuth2User.getAttribute(userNameAttributeName)).toString();
        Account _siteUser = null;
        try {
            _siteUser = this.accountService.getAccount(username);
        }
        catch(Exception e) {
            _siteUser = this.accountService.create(username, null, "", provider, username);
        }

        // attribute 정의
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        //attributes.put("name", username);


        // DefaultOAuth2User 객체를 소유하여, 기능 구현되므로 생성자로 넘겨주기.
        return new CustomOAuth2User(new DefaultOAuth2User(authorities, attributes, userNameAttributeName));
    }
}
