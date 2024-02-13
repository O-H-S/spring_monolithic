package com.ohs.monolithic.board.utils;


import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.AuthenticationType;
import com.ohs.monolithic.user.domain.UserRole;
import com.ohs.monolithic.user.dto.LocalAppUser;
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
            .nickname("securityUser")
            .authenticationType(AuthenticationType.Local)
            .email("user@security.com")
            .role(customUser.role())
            .build();

    //BoardTestUtils.setEntityID(account, "id", Long.valueOf (customUser.accountId()));
    LocalAppUser principal = new LocalAppUser(account , customUser.username(), "pass", authorities); //

    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, "password", authorities);
    context.setAuthentication(auth);

    return context;
  }
}