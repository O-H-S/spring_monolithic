package com.ohs.monolithic.utils;


import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.AuthenticationType;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(customUser.authorities());
    Account account = Account.builder()
            .nickname(customUser.nickname())
            .authenticationType(AuthenticationType.Local)
            .email("user@security.com")
            .role(customUser.role())
            .build();

    BoardTestUtils.setEntityID(account, "id", 1L);
    LocalAppUser principal = new LocalAppUser(account.getId(), customUser.username(), "pass", customUser.nickname(),authorities); //

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "password", authorities);
    context.setAuthentication(auth);

    WithMockCustomUserContext.setAccount(account);
    WithMockCustomUserContext.setAppUser(principal);
    return context;
  }
}