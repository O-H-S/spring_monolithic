package com.ohs.monolithic.account.service;

import com.ohs.monolithic.account.dto.AccountNotificationResponse;
import com.ohs.monolithic.account.domain.AccountNotification;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface NotificationResponseBuilder<T extends AccountNotification> {
  AccountNotificationResponse build(AccountNotification notification);
  @SuppressWarnings("unchecked")
  default Class<T> getNotificationClass() {
    Type type = ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    return (Class<T>) type;
  }
}