package com.ohs.monolithic.account.service;


import com.ohs.monolithic.account.dto.AccountNotificationPaginationResponse;
import com.ohs.monolithic.account.dto.AccountNotificationResponse;
import com.ohs.monolithic.account.repository.AccountNotificationRepository;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.domain.AccountNotification;
import com.ohs.monolithic.notification.event.NotificationCollectEvent;
import com.ohs.monolithic.notification.service.NotificationService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
public class AccountNotificationService extends NotificationService<AccountNotification> {

  @Getter
  @Setter
  public static class AccountNotificationCollectEvent extends NotificationCollectEvent {
    Long accountId;
    public AccountNotificationCollectEvent(LocalDateTime lastTime, Long accountId) {
      super(lastTime);
      this.accountId = accountId;
    }
  }
  private final AccountNotificationRepository accountNotificationRepository;
  private final AccountRepository accountRepository;
  private final ApplicationContext applicationContext;
  private final Map<Class<? extends AccountNotification>, NotificationResponseBuilder<? extends AccountNotification>> responseBuilders = new HashMap<>();

  public AccountNotificationService(ApplicationEventPublisher eventPublisher,
                                    AccountNotificationRepository accountNotificationRepository, AccountRepository accountRepository, ApplicationContext applicationContext) {
    super(eventPublisher);
    this.accountNotificationRepository = accountNotificationRepository;
    this.accountRepository = accountRepository;
    this.applicationContext = applicationContext;
  }

  // Notification의 종류에 따라 응답 메세지의 처리를 다르게 해야하므로, 각 타입에 맞게 정의된 builder를 불러온다.
  @PostConstruct
  public void initializeResponseBuilders() {
    Map<String, NotificationResponseBuilder> builderBeans = applicationContext.getBeansOfType(NotificationResponseBuilder.class);
    for (NotificationResponseBuilder<?> builder : builderBeans.values()) {
      responseBuilders.put(builder.getNotificationClass(), builder);
    }

  }



  @Transactional(readOnly = true)
  public void collectNotification(AppUser user){
    var lastNotification = accountNotificationRepository.findTopByAccountOrderByTimestampDesc(accountRepository.getReferenceById(user.getAccountId()));
    LocalDateTime lastTime = LocalDateTime.of(1000, 1, 1, 0, 0); // LocalDateTime.Min으로 하려 했으나, DB에서 허용되는 범위를 넘어감.
    if(lastNotification != null)
      lastTime = lastNotification.getTimestamp();

    invokeCollectEvent(new AccountNotificationCollectEvent(lastTime, user.getAccountId()));
  }

  @Transactional(readOnly = true)
  public AccountNotificationPaginationResponse getNotifications(AppUser user, LocalDateTime lastDateTime){
    collectNotification(user); // 동기적으로 알림 수집됨.

    if(lastDateTime == null)
      lastDateTime = LocalDateTime.now();

    List<AccountNotification> results = accountNotificationRepository.findTop10ByAccountAndViewedAndTimestampLessThanOrderByTimestampDesc(accountRepository.getReferenceById(user.getAccountId()),Boolean.FALSE, lastDateTime);

    List<AccountNotificationResponse> responses = new ArrayList<>(results.size());
    for (AccountNotification notification : results) {
      NotificationResponseBuilder<? extends AccountNotification> builder = responseBuilders.get(notification.getClass());
      if (builder != null) {
        AccountNotificationResponse response = builder.build(notification);
        if(response != null)
          responses.add(response);
        else {

        }
      } else {
        throw new IllegalArgumentException("No response builder found for notification type: " + notification.getClass().getName());
      }
    }

    AccountNotificationPaginationResponse response = new AccountNotificationPaginationResponse();
    response.setNotifications(responses);
    response.setNextCursor(responses.isEmpty() || responses.size() < 10 ? null : responses.get(responses.size()-1).getTimestamp());

    return response;
  }

  @Transactional
  public void markNotificationAsViewed(Long notificationId){
    Optional<AccountNotification> notification = accountNotificationRepository.findById(notificationId);
    if(notification.isEmpty())
      throw new EntityNotFoundException("해당 알림이 존재하지 않습니다.");

    notification.get().setViewed(true);
    accountNotificationRepository.save(notification.get());

  }
}