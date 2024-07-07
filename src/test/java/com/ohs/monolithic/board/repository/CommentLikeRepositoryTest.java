package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.utils.RepositoryTestBase;
import com.ohs.monolithic.utils.RepositoryTestHelper;
import com.ohs.monolithic.common.configuration.QuerydslConfig;
import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.utils.RepositoryTestWithH2;
import groovy.lang.Tuple3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class CommentLikeRepositoryTest extends RepositoryTestWithH2 {

  @Autowired // datajpatest 어노테이션이 관련 context를 로드하기 때문에 가능하다.
  CommentLikeRepository commentLikeRepository;

  @Autowired
  CommentRepository commentRepository;




  @AfterEach
  void teardown(){

    // @DataJpaTest는 각 테스트 후 롤백되므로 db 초기화는 필요 없지만 헬퍼 객체가 내부적으로 사용하는 변수들이 존재함.
    helper.resetCounts();
  }


  @Test
  @DisplayName("findCommentLikeWithLock(Comment, Account) : 존재하지 않는 좋아요 조회")
  public void findCommentLike_notfound(){

    Tuple3<Post, Account, Board> givens = helper.createPostAccountBoard();
    Comment testComment = helper.writeCommentTo(givens.getV1(),givens.getV2());


    Optional<CommentLike> commentLikeOp = commentLikeRepository.findCommentLikeWithLock(testComment.getId(), givens.getV2().getId());

    assertThat(commentLikeOp).isNotNull();
    assertThat(commentLikeOp.isEmpty()).isTrue();

  }

  @Test
  @DisplayName("findCommentLikeWithLock(Comment, Account) : 존재하는 좋아요 조회")
  public void findCommentLike_found(){

    //given
    Tuple3<Post, Account, Board> givens  = helper.createPostAccountBoard();

    Comment testComment = helper.writeCommentTo(givens.getV1(),givens.getV2());

    CommentLike testLike = helper.likeTo(testComment, givens.getV2());
    //when

    Optional<CommentLike> commentLikeOp = commentLikeRepository.findCommentLikeWithLock(testComment.getId(), givens.getV2().getId());

    // then
    assertThat(commentLikeOp).isNotNull();
    assertThat(commentLikeOp.isEmpty()).isFalse();

  }



}
