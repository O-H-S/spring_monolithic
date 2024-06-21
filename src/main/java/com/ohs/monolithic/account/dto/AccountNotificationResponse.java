package com.ohs.monolithic.account.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public abstract class AccountNotificationResponse {

  Long id;
  protected String type;
  Boolean viewed;
  LocalDateTime timestamp;
  /*public static AccountNotificationResponse of(AccountNotification notification, String type){
    AccountNotificationResponse newResponse = new AccountNotificationResponse();

    newResponse.setType(type);
    newResponse.setViewed(notification.getViewed());
    newResponse.setTimestamp(notification.getTimestamp());
    return newResponse;
  }*/
}
