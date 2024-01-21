package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import com.ohs.monolithic.configuration.QuerydslConfig;
import com.ohs.monolithic.user.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest

@Tag("base")
@Tag("integrate-limited")
@Import(QuerydslConfig.class)
public class CommentLikeRepositoryTest {

  @Autowired // datajpatest 어노테이션이 관련 context를 로드하기 때문에 가능하다.
  CommentLikeRepository commentLikeRepository;

  @Autowired
  CommentRepository commentRepository;
  @Autowired
  TestEntityManager testEntityManager;

  @Test
  @DisplayName("findCommentLikeWithLock(Comment, Account) : 존재하지 않는 좋아요 조회")
  public void findCommentLike_notfound(){

    Triple<Post, Account, Board> givenEntities  =  BoardTestUtils.createSimplePostAccountBoard();
    testEntityManager.merge(givenEntities.b);
    testEntityManager.merge(givenEntities.c);
    testEntityManager.merge(givenEntities.a);

    Comment testComment = Comment.builder()
            .post(givenEntities.a)
            .content("test")
            .author(givenEntities.b)
            .build();
    commentRepository.save(testComment);

    Optional<CommentLike> commentLikeOp = commentLikeRepository.findCommentLikeWithLock(testComment, givenEntities.b);

    assertThat(commentLikeOp).isNotNull();
    assertThat(commentLikeOp.isEmpty()).isTrue();

  }

  @Test
  @DisplayName("findCommentLikeWithLock(Comment, Account) : 존재하는 좋아요 조회")
  public void findCommentLike_found(){

    //given
    Triple<Post, Account, Board> givenEntities  =  BoardTestUtils.createSimplePostAccountBoard();
    testEntityManager.merge(givenEntities.b);
    testEntityManager.merge(givenEntities.c);
    testEntityManager.merge(givenEntities.a);

    Comment testComment = Comment.builder()
            .post(givenEntities.a)
            .content("test")
            .author(givenEntities.b)
            .build();
    commentRepository.save(testComment);

    CommentLike testLike = CommentLike.builder()
            .comment(testComment)
            .member(givenEntities.b)
                    .valid(Boolean.TRUE)
                            .build();
    commentLikeRepository.save(testLike);
    //when

    Optional<CommentLike> commentLikeOp = commentLikeRepository.findCommentLikeWithLock(testComment, givenEntities.b);

    // then
    assertThat(commentLikeOp).isNotNull();
    assertThat(commentLikeOp.isEmpty()).isFalse();

  }



}
