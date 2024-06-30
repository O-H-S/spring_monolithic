package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.WithMockCustomUser;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostApiControllerIntegrationTest  extends IntegrationTestWithH2 {

   /*================================================================================

        게시글 생성

  ================================================================================*/
  @Test
  @DisplayName("POST /api/{boardId}/posts: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void createBoard_0() throws Exception {
    initSecurityUserAccount();
    //given
    BoardResponse targetBoard = helper.simpleBoard();
    PostForm form = PostForm.builder()
            .subject("Hello world")
            .content("hi")
            .build();

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .post(
                            String.format("/api/%d/posts", targetBoard.getId() )
                    )
                    .with(csrf())
                    .content(gson.toJson(form))
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isCreated());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("posts/post/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }


}
