package com.ohs.monolithic.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
public class OAuth2Credential {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "account_id")
  private Account account;

  @Setter
  @Column(nullable = false)
  private String provider;  // 소셜 로그인 제공자 (예: google, facebook, kakao)

  @Setter
  @Column(nullable = false)
  private String providerId;  // 소셜 로그인 제공자에서의 사용자 ID

  @Builder
  public OAuth2Credential(Account account, String provider, String providerId){
    this.account = account;
    this.provider = provider;
    this.providerId = providerId;
  }

}
