package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.user.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT cl FROM CommentLike cl WHERE cl.comment = :comment AND cl.member = :member")
  Optional<CommentLike> findCommentLikeWithLock(Comment comment, Account member);

}
