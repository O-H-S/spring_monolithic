package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.repository.CommentLikeRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.user.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


// 댓글의 좋아요 기능을 담당
// 좋아요 활성화/ 비활성화를 
@Service
@RequiredArgsConstructor
public class CommentLikeService {
  final private CommentRepository commentRepository;
  final private CommentLikeRepository commentLikeRepository;



  // Test Exists(Itegration), 동시성 테스트 필요
  @Transactional
  public Boolean likeComment(Comment comment, Account member) {
    //commentLikeRepository.findB
    Optional<CommentLike> cLikeOp = commentLikeRepository.findCommentLikeWithLock(comment, member);
    if (cLikeOp.isPresent()) {
      CommentLike cLike = cLikeOp.get();
      if (!cLike.getValid()) {
        cLike.setValid(Boolean.TRUE);
        commentLikeRepository.save(cLike);
        commentRepository.addLikeCount(comment.getId(), 1L);
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    CommentLike newCommentLike = CommentLike.builder()
            .comment(comment)
            .member(member)
            .valid(Boolean.TRUE)
            .build();

    commentLikeRepository.save(newCommentLike);
    commentRepository.addLikeCount(comment.getId(), 1L);
    return Boolean.TRUE;
  }

  // Test Exists(Itegration), 동시성 테스트 필요
  @Transactional
  public Boolean unlikeComment(Comment comment, Account member) {
    //commentLikeRepository.findB
    Optional<CommentLike> cLikeOp = commentLikeRepository.findCommentLikeWithLock(comment, member);
    if (cLikeOp.isPresent()) {
      CommentLike cLike = cLikeOp.get();
      if (cLike.getValid()) {
        cLike.setValid(Boolean.FALSE);
        commentLikeRepository.save(cLike);
        commentRepository.addLikeCount(comment.getId(), -1L);
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;

  }


}
