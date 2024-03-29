package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.utils.BoardIntegrationTestHelper;
import com.ohs.monolithic.board.utils.IntegrationTestBase;
import com.ohs.monolithic.user.domain.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


public class CommentLikeServiceIntegrationTest extends IntegrationTestBase {

  @Autowired
  private CommentLikeService commentLikeService;
  @Autowired
  private CommentService commentService;

  @Test
  @DisplayName("likeComment(Comment, Account) : 최초 댓글 추천시 True 리턴, 이미 추천되어 있으면 False 리턴 ")
  void likeComment_0(){
    //given
    Triple<BoardResponse, Account, Post> givens = helper.InitDummy_BoardAccountPost();
    Comment comment = commentService.create(givens.c, "test", givens.b);

    //when
    Boolean changed = commentLikeService.likeComment(comment.getId(), givens.b.getId());
    Boolean changed2 = commentLikeService.likeComment(comment.getId(), givens.b.getId());

    //then
    assertThat(changed).isTrue(); // 추천됨
    assertThat(commentService.getComment(comment.getId()).getLikeCount()).isEqualTo(1L);

    assertThat(changed2).isFalse(); // 이미 추천되어있으므로 무시.
  }

  @Test
  @DisplayName("unlikeComment(Comment, Account) : 댓글 추천 취소, 추천 된 상태여야만 True 리턴 ")
  void unlikeComment_0(){
    //given
    Triple<BoardResponse, Account, Post> givens = helper.InitDummy_BoardAccountPost();

    Comment comment = commentService.create(givens.c, "test", givens.b);
    commentLikeService.likeComment(comment.getId(), givens.b.getId());

    Comment comment_ignored = commentService.create(givens.c, "test2", givens.b);

    //when
    Boolean changed = commentLikeService.unlikeComment(comment.getId(), givens.b.getId());
    Boolean changed_2 = commentLikeService.unlikeComment(comment.getId(), givens.b.getId());

    Boolean changed_3 = commentLikeService.unlikeComment(comment_ignored.getId(), givens.b.getId());

    //then
    assertThat(changed).isTrue(); // 추천 취소 성공
    assertThat(changed_2).isFalse(); // 추천 취소된 상태이므로, 무시
    assertThat(changed_3).isFalse(); // 추천한 적 없으므로, 무시

    assertThat(commentService.getComment(comment.getId()).getLikeCount()).isEqualTo(0L);
    assertThat(commentService.getComment(comment_ignored.getId()).getLikeCount()).isEqualTo(0L);
  }



}
