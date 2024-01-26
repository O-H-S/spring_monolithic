package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostLike;
import com.ohs.monolithic.user.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT cl FROM PostLike cl WHERE cl.post.id = :postId AND cl.user = :member")
  Optional<PostLike> findPostLikeWithLock(Long postId, Account member);

  Boolean existsByPostAndUserAndValidIsTrue(Post post, Account member);

}
