package com.ohs.monolithic.auth.domain;

import com.ohs.monolithic.account.domain.Account;

import java.io.Serializable;


// 만약, 인증 이후에 해당 유저의 Account 엔티티가 변경된다면, 데이터 불일치가 발생함.
// Account 직접 참조 제거하기.
public interface AppUser /*extends Serializable */{
  Long getAccountId();
  String getNickname();
  Boolean isAdmin();

}
