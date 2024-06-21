package com.ohs.monolithic.problem.service;

import com.ohs.monolithic.account.dto.AccountNotificationResponse;
import com.ohs.monolithic.account.service.NotificationResponseBuilder;
import com.ohs.monolithic.account.domain.AccountNotification;
import com.ohs.monolithic.problem.domain.ProblemPostNotification;
import com.ohs.monolithic.problem.dto.ProblemPostNotificationResponse;
import org.springframework.stereotype.Component;

@Component
public class ProblemPostNotificationResponseBuilder implements NotificationResponseBuilder<ProblemPostNotification> {
  @Override
  public AccountNotificationResponse build(AccountNotification notification) {
    ProblemPostNotification casted = (ProblemPostNotification)notification;
    return ProblemPostNotificationResponse.of(casted);
  }

}