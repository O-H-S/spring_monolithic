package com.ohs.monolithic.notification.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Generic을 사용하려 했으나, 관련 문서가 없고 테이블 이름 중복 이슈가 있어서 @MappedSuperclass 사용.
@MappedSuperclass
@Getter
public abstract class NotificationBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Setter
  @Column(nullable = false)
  private Boolean viewed;
}