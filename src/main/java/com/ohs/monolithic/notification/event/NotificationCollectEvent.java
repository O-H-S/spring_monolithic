package com.ohs.monolithic.notification.event;


import com.ohs.monolithic.notification.domain.NotificationBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
// generic으로 하려 했으나, eventlisener에서 인식하지 못함.
// https://stackoverflow.com/questions/71452445/eventlistener-for-generic-events-with-spring
public abstract class NotificationCollectEvent {

  LocalDateTime lastTime; // 마지막 알림의 시간.

}
