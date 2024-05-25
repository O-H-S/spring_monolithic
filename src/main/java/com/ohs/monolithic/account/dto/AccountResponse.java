package com.ohs.monolithic.account.dto;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@NoArgsConstructor
@Getter
@Setter
public class AccountResponse {
  public Long id;
  public String nickname;
  public String email;
  public String profileImage;


  // Jackson은 스프링 부트의 기본 JSON 매핑 라이브러리
  // 기본적으로, LocalDateTime 객체는 ISO-8601 날짜 및 시간 포맷 (yyyy-MM-dd'T'HH:mm:ss.SSS)으로 변환
  //public LocalDateTime createDate;로 해봤으나 실패함

  public String createDate;
  public Boolean admin;
  //

  public static AccountResponse of(Account source){
    AccountResponse response = new AccountResponse();


    response.id = source.getId();
    response.nickname = source.getNickname();
    response.email = source.getEmail();
    response.profileImage = source.getProfileImage();



    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    response.createDate = source.getCreateDate().format(formatter);

    response.admin = source.getRole() == UserRole.ADMIN;

    return response;

  }

}
