package com.ohs.monolithic.user.dto;

import com.ohs.monolithic.user.domain.Account;

public class AccountResponse {
  public Long id;
  public String nickname;
  public String email;
  public static AccountResponse of(Account source){
    AccountResponse response = new AccountResponse();


    response.id = source.getId();
    response.nickname = source.getNickname();
    response.email = source.getEmail();



    return response;


  }

}