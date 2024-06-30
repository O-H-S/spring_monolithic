package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.utils.WithMockCustomUserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostDetailApiControllerIntegrationTest  extends IntegrationTestWithH2 {

   /*================================================================================

        특정 게시글 조회

  ================================================================================*/

  @Test
  @DisplayName("GET /api/posts/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void getPost_0() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(), writer,1);
    PostDetailResponse targetPost = posts.get(0);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get(
                            String.format("/api/posts/%d", posts.get(0).getId() )
                    )
                    .param("determineLiked", "true")
                    .param("determineMine", "true")
    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/getById/succeeded-recommended2",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("GET /api/posts/{id}: 성공(NO PARAMS) - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void getPost_1() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(), writer,1);
    PostDetailResponse targetPost = posts.get(0);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get(
                            String.format("/api/posts/%d", posts.get(0).getId() )
                    )
    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/getById/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }






  /*================================================================================

        특정 게시글 삭제

  ================================================================================*/
  @Test
  @DisplayName("DELETE /api/posts/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_0() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/posts/%d", posts.get(0).getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/delete/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("DELETE /api/posts/{id}: 글 작성자가 아니면 실패 -   ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void deletePost_0() throws Exception {
    Account writer = helper.simpleAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/posts/%d", posts.get(0).getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isForbidden());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/delete/failed-access",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  /*================================================================================

        특정 게시글 수정

  ================================================================================*/
  @Test
  @DisplayName("PUT /api/posts/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void modifyPost_0() throws Exception {
    Account writer = initSecurityUserAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);

    PostForm form = PostForm.builder()
            .subject("New Title")
            .content("New Content")
            .build();

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .put(
                            String.format("/api/posts/%d", posts.get(0).getId() )
                    )
                    .content(gson.toJson(form))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/put/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }


  /*================================================================================

        특정 게시글 추천 및 취소

  ================================================================================*/
  @Test
  @DisplayName("POST /api/posts/{id}/postLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void likePost_0() throws Exception {
    Account voter = initSecurityUserAccount();
    Account writer = helper.simpleAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/posts/%d/postLikes", posts.get(0).getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(true));
    result.andExpect(jsonPath("$.count").value(1));

    result.andDo(document("postLikes/post/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("POST /api/posts/{id}/postLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void likePost_1() throws Exception {
    Account voter = initSecurityUserAccount();
    Account writer = helper.simpleAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);
    PostDetailResponse targetPost = posts.get(0);
    helper.postLikeService.likePost(targetPost.getId(), WithMockCustomUserContext.getAppUser());

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/posts/%d/postLikes", targetPost.getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(false));
    result.andExpect(jsonPath("$.count").value(1));

    result.andDo(document("postLikes/post/succeeded-unchanged",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }


  @Test
  @DisplayName("DELETE /api/posts/{id}/postLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void unlikePost_0() throws Exception {
    Account voter = initSecurityUserAccount();
    Account writer = helper.simpleAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);
    PostDetailResponse targetPost = posts.get(0);
    helper.postLikeService.likePost(targetPost.getId(), WithMockCustomUserContext.getAppUser());

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/posts/%d/postLikes", posts.get(0).getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(true));
    result.andExpect(jsonPath("$.count").value(0));

    result.andDo(document("postLikes/delete/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("DELETE /api/posts/{id}/postLikes: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void unlikePost_1() throws Exception {
    Account voter = initSecurityUserAccount();
    Account writer = helper.simpleAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    List<PostDetailResponse> posts =  helper.simplePost(targetBoard.getId(),writer,1);
    PostDetailResponse targetPost = posts.get(0);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .delete(
                            String.format("/api/posts/%d/postLikes", posts.get(0).getId() )
                    )
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    result.andExpect(jsonPath("$.changed").value(false));
    result.andExpect(jsonPath("$.count").value(0));

    result.andDo(document("postLikes/delete/succeeded-unchanged",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }




}
