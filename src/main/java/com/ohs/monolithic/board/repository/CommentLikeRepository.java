package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.user.domain.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT cl FROM CommentLike cl WHERE cl.comment.id = :commentId AND cl.member.id = :memberId")
  Optional<CommentLike> findCommentLikeWithLock(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

}
