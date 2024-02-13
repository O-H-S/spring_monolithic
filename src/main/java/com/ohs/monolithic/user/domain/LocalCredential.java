package com.ohs.monolithic.user.domain;

import com.ohs.monolithic.board.domain.Board;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Getter
@Entity
@NoArgsConstructor
public class LocalCredential {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "account_id")
  private Account account;

  @Setter
  @Column(unique = true, nullable = false)
  private String username;

  @Setter
  @Column(nullable = false)
  private String password;

  @Builder
  public LocalCredential(Account account , String username, String password)
  {
    this.account = account;
    this.username = username;
    this.password = password;

  }

}
