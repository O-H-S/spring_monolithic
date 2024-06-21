package com.ohs.monolithic.notification.service;


import com.ohs.monolithic.notification.domain.NotificationBase;
import com.ohs.monolithic.notification.event.NotificationCollectEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@AllArgsConstructor
public abstract class NotificationService<T extends NotificationBase> {
  protected final ApplicationEventPublisher eventPublisher;

  protected void invokeCollectEvent(NotificationCollectEvent event) {
    eventPublisher.publishEvent(event);
  }


}
