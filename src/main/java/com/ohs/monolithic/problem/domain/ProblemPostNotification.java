package com.ohs.monolithic.problem.domain;


import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.account.domain.AccountNotification;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "problem_post_notifications"
)
@Setter
@Getter
@NoArgsConstructor
public class ProblemPostNotification extends AccountNotification {

  @JoinColumn(name = "problem_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private Problem problem;

  @JoinColumn(name = "post_id", nullable = false)
  @ManyToOne(fetch = FetchType.EAGER)
  private Post post;

  @Column(nullable = false)
  private Boolean valid; // 생성 되었다면 true, 삭제면 false


  @Builder
  public ProblemPostNotification(LocalDateTime timestamp, Account account, Problem problem, Post post, Boolean valid){
    if(timestamp == null)
      timestamp = LocalDateTime.now();


    this.setTimestamp(timestamp);
    this.setViewed(Boolean.FALSE);
    this.setAccount(account);
    this.problem = problem;
    this.post = post;
    this.valid = valid != null ? Boolean.TRUE : Boolean.FALSE;

  }

}