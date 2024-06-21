package com.ohs.monolithic.problem.domain;


import com.ohs.monolithic.account.domain.Account;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "problembookmark", indexes = {
        @Index(name = "idx_problembookmark_account_id_problem_id", columnList = "account_id, problem_id"), //
})
public class ProblemBookmark {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @JoinColumn(name = "account_id" , nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Account account;

  @Setter
  @JoinColumn(name = "problem_id" , nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Problem problem;

  @Setter
  @Column(name = "bookmark_type", nullable = true)
  @Enumerated(EnumType.STRING)
  private ProblemBookmarkType bookmarkType;

/*
  @Setter
  private Boolean nofifyPost;

  @Setter
  private Boolean nofifyComment;*/


}
