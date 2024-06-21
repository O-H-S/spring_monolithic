package com.ohs.monolithic.problem.domain;


import com.ohs.monolithic.board.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "problemposthistory", indexes = {
        @Index(name = "idx_problemposthistory_problem_id_create_date", columnList = "problem_id, create_date DESC"), //
})
public class ProblemPostHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @JoinColumn(name = "problem_id" , nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Problem problem;

  @Setter
  @JoinColumn(name = "post_id" , nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @Setter
  @Column( nullable = false)
  private Boolean valid; // true 이면 생성, false이면 삭제

  @Setter
  @Column(name = "create_date")
  private LocalDateTime createDate; // 언제 작성/ 삭제 되었는지를 나타냄
}
