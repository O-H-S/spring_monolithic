package com.ohs.monolithic.account.domain;


import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.notification.domain.NotificationBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// @Inheritance 애노테이션을 명시적으로 정의하지 않으면, JPA는 상속 전략을 SINGLE_TABLE로 간주
@Entity
@Getter
@Setter
@Table(name = "account_notifications", indexes = {
        @Index(name = "idx_pp_notifications", columnList = "account_id,  timestamp DESC, viewed")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class AccountNotification extends NotificationBase {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;
}
