package com.ohs.monolithic.account.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AccountNotificationPaginationResponse {
  List<AccountNotificationResponse> notifications;
  LocalDateTime nextCursor;

}
