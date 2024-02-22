package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.repository.CommentLikeRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


// 댓글의 좋아요 기능을 담당
// 좋아요 활성화/ 비활성화를 
@Service
@RequiredArgsConstructor
public class CommentLikeService {
  final private CommentRepository commentRepository;
  final private CommentService commentService;
  final private CommentLikeRepository commentLikeRepository;
  final private AccountRepository accountRepository;


  // Test Exists(Itegration), 동시성 테스트 필요
  @Transactional
  public Boolean likeComment(Long commentId, Long memberId) {
    //commentLikeRepository.findB
    Optional<CommentLike> cLikeOp = commentLikeRepository.findCommentLikeWithLock(commentId, memberId);
    if (cLikeOp.isPresent()) {
      CommentLike cLike = cLikeOp.get();
      if (!cLike.getValid()) {
        cLike.setValid(Boolean.TRUE);

        commentLikeRepository.save(cLike);
        commentRepository.addLikeCount(commentId,  1L);
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    CommentLike newCommentLike = CommentLike.builder()
            .comment(commentRepository.getReferenceById(commentId))
            .member(accountRepository.getReferenceById(memberId))
            .valid(Boolean.TRUE)
            .build();

    commentLikeRepository.save(newCommentLike);
    commentRepository.addLikeCount(commentId, 1L);
    return Boolean.TRUE;
  }

  @Transactional
  public Pair<Boolean, Long> likeCommentEx(Long commentId, Long memberId){
    Comment targetComment = commentService.getComment(commentId);
    Boolean result = this.likeComment(commentId, memberId);
    return Pair.of(result, targetComment.getLikeCount());
  }


  // Test Exists(Itegration), 동시성 테스트 필요
  @Transactional
  public Boolean unlikeComment(Long commentId, Long memberId) {
    //commentLikeRepository.findB
    Optional<CommentLike> cLikeOp = commentLikeRepository.findCommentLikeWithLock(commentId, memberId);
    if (cLikeOp.isPresent()) {
      CommentLike cLike = cLikeOp.get();
      if (cLike.getValid()) {
        cLike.setValid(Boolean.FALSE);
        commentLikeRepository.save(cLike);
        commentRepository.addLikeCount(commentId, -1L);
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  @Transactional
  public Pair<Boolean, Long> unlikeCommentEx(Long commentId, Long memberId){
    Comment targetComment = commentService.getComment(commentId);
    Boolean result = this.unlikeComment(commentId, memberId);
    return Pair.of(result, targetComment.getLikeCount());
  }





}
