package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.CommentCreationResponse;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.account.domain.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



class CommentDetailApiControllerIntegrationTest extends IntegrationTestBase {
  /*================================================================================

        특정 댓글 변경

  ================================================================================*/

  @Test
  @DisplayName("PUT /api/comments/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void modifyPost_0() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "Test Comment", writer.getId());

    CommentForm form = new CommentForm();
    form.setContent("modified content");


    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .put(
                            String.format("/api/comments/%d", targetComment.getCommentData().getId() )
                    )
                    .content(gson.toJson(form))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("comments/put/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("PUT /api/comments/{id}: 자신의 댓글이 아니면 실패 -   ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void modifyPost_1() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "Test Comment", givens.b.getId());

    CommentForm form = new CommentForm();
    form.setContent("modified content");


    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .put(
                            String.format("/api/comments/%d", targetComment.getCommentData().getId() )
                    )
                    .content(gson.toJson(form))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isForbidden());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("comments/put/failed-denied",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }




  /*================================================================================

        댓글 삭제

  ================================================================================*/

  @Test
  @DisplayName("DELETE /api/comments/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void deleteComment_0() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "Test Comment", writer.getId());
    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/comments/%d", targetComment.getCommentData().getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("comments/delete/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }




   /*================================================================================

        댓글 추천

  ================================================================================*/

  @Test
  @DisplayName("POST /api/comments/{commentID}/commentLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_0() throws Exception {
    initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost("dum", "minsu", "dum");
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "test", givens.b.getId());

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getCommentData().getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(true));
    result.andDo(document("commentLikes/post/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("POST /api/comments/{commentID}/commentLikes: 이미 추천한 상태면 changed 는 false  - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_1() throws Exception {
    Account viewer = initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost("dum", "minsu", "dum");
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "test", givens.b.getId());

    helper.commentLikeService.likeComment(targetComment.getCommentData().getId(), viewer.getId());

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getCommentData().getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then

    result.andExpect(status().isOk());
    result.andExpect(jsonPath("$.changed").value(false));

    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andDo(document("commentLikes/post/succeeded-alreadyliked",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("POST /api/comments/{commentID}/commentLikes: 인증된 접근이 아니면, 실패 - 403  ")
  @WithAnonymousUser
  public void createBoard_2() throws Exception {
    initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "test", givens.b.getId());


    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getCommentData().getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then

    result.andExpect(status().isForbidden());
    result.andDo(document("commentLikes/post/failed-notlogged",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("POST /api/comments/{commentID}/commentLikes: 해당 댓글이 없으면 실패  - 404  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_3() throws Exception {
    initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost("dum", "minsu", "dum");

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            "/api/comments/-1/commentLikes"
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then

    result.andExpect(status().isNotFound());


    result.andDo(document("commentLikes/post/failed-notfound",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }


    /*================================================================================

        댓글 추천 취소

  ================================================================================*/

  @Test
  @DisplayName("DELETE /api/comments/{commentID}/commentLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void unlikeComment_0() throws Exception {
    Account operator = initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost("dum", "dum", "dum");
    CommentCreationResponse targetComment = helper.commentService.createByID(givens.c.getId(), "test", givens.b.getId());
    helper.commentLikeService.likeComment(targetComment.getCommentData().getId(), operator.getId());

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/comments/%d/commentLikes", targetComment.getCommentData().getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(true));
    result.andDo(document("commentLikes/delete/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }



}