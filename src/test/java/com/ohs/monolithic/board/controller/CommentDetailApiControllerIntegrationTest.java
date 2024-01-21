package com.ohs.monolithic.board.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.utils.BoardIntegrationTestHelper;
import com.ohs.monolithic.user.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Tag("base")
@Tag("integrate")
class CommentDetailApiControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  private Gson gson; // json 직렬화,역직렬화

  @Autowired
  BoardIntegrationTestHelper initializer;

  @BeforeEach
  public void init() {
    gson = new Gson();
  }

  @AfterEach
  public void release(){
    initializer.release();
  }

   /*================================================================================

        댓글 추천

  ================================================================================*/

  @Test
  @DisplayName("POST /api/comments/{commentID}/commentLikes: 성공 - 200  ")
  @WithMockUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_0() throws Exception {
    //given
    Triple<BoardResponse, Account, Post> givens = initializer.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");
    Comment targetComment = initializer.commentService.create(givens.c, "test", givens.b);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());;

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
  @WithMockUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_1() throws Exception {
    //given
    Triple<BoardResponse, Account, Post> givens = initializer.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");
    Comment targetComment = initializer.commentService.create(givens.c, "test", givens.b);

    initializer.commentLikeService.likeComment(targetComment, givens.b);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getId() )
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
    //given
    Triple<BoardResponse, Account, Post> givens = initializer.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");
    Comment targetComment = initializer.commentService.create(givens.c, "test", givens.b);


    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/comments/%d/commentLikes", targetComment.getId() )
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
  @WithMockUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_3() throws Exception {
    //given
    Triple<BoardResponse, Account, Post> givens = initializer.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");

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
  @WithMockUser(username = "hyeonsu", authorities = "USER")
  public void unlikeComment_0() throws Exception {
    //given
    Triple<BoardResponse, Account, Post> givens = initializer.InitDummy_BoardAccountPost("dum", "hyeonsu", "dum");
    Comment targetComment = initializer.commentService.create(givens.c, "test", givens.b);
    initializer.commentLikeService.likeComment(targetComment, givens.b);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/comments/%d/commentLikes", targetComment.getId() )
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