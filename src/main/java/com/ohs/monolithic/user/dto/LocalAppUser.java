package com.ohs.monolithic.user.dto;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.dto.AppUser;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class LocalAppUser extends User implements AppUser {

  // 리팩토링 필요, Account 도메인 객체를 직접 가지기 보다는 dto를 사용하기.

  @Setter
  private Account account;
  public LocalAppUser(Account account, String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
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
