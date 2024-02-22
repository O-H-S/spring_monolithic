package com.ohs.monolithic.user.dto;

import com.ohs.monolithic.user.domain.Account;

import java.security.Principal;


// 만약, 인증 이후에 해당 유저의 Account 엔티티가 변경된다면, 데이터 불일치가 발생함.
public interface AppUser {
  Long getAccountId();
  Account getAccount();
  void setAccount(Account account);
}
