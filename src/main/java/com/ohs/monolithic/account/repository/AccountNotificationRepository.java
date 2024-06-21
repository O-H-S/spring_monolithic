package com.ohs.monolithic.account.repository;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.AccountNotification;
import com.ohs.monolithic.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountNotificationRepository extends NotificationRepository<AccountNotification, Long> {
  AccountNotification findTopByAccountOrderByTimestampDesc(Account account);
  List<AccountNotification> findTop10ByAccountAndViewedAndTimestampLessThanOrderByTimestampDesc(
          Account account, Boolean viewed, LocalDateTime timestamp
  );




}
