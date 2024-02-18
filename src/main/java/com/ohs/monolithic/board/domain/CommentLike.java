package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.user.domain.Account;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "commentlike", indexes = {
        @Index(name = "idx_commentlike_comment_id_member_id", columnList = "comment_id, member_id"), //
})
public class CommentLike {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) //
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Account member;

  @Setter
  private Boolean valid; // 한번 더 누르면, 삭제하는 대신 값을 변경하여 비활성화 한다.
  private LocalDateTime createDate;
  @Setter
  private LocalDateTime updateDate; // 최초 좋아요를 누르거나, 다시 한번 눌러 비활성

  @Builder/*(builderClassName = "ByPrimitive", builderMethodName = "ByPrimitive")*/
  public CommentLike(Comment comment, Account member, Boolean valid){
    Assert.notNull(comment, " comment must not be null");
    Assert.notNull(member, " member must not be null");
    Assert.notNull(valid, "valid must not be null");

    this.comment = comment;
    this.member = member;
    this.valid = valid;

    this.createDate = LocalDateTime.now();
    this.updateDate = this.createDate;// LocalDateTime 불변 객체이므로 문제 없다.
  }


}
