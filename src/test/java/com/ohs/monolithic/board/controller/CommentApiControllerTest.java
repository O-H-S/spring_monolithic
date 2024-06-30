package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.account.domain.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentApiControllerTest extends IntegrationTestWithH2 {

  /*================================================================================

          댓글 생성

    ================================================================================*/
  @Test
  @DisplayName("POST /api/{postId}/comments: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_0() throws Exception {
    initSecurityUserAccount();
    //given
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();

    PostDetailResponse targetPost = givens.c;

    CommentForm form = new CommentForm();
    form.setContent("Test Content");

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/%d/comments", targetPost.getId() )
                    )
                    .with(csrf())
                    .content(gson.toJson(form))
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isCreated());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("comments/post/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }





}
