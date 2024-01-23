package com.ohs.monolithic.board.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.SecurityConfigForUnitTest;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostViewService;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PostReadController.class)
@EnableMethodSecurity(prePostEnabled = true)
@Import(SecurityConfigForUnitTest.class)
@Tag("base")
@Tag("unit")
class PostReadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PostReadService postReadService;
  @MockBean
  private PostViewService postViewService;
  @MockBean
  private AccountService accountService;
  @MockBean
  private CommentService commentService; // legacy
  private Gson gson; // json 직렬화,역직렬화

  @BeforeEach
  public void init() {
    gson = new Gson();
  }

  @Test
  @DisplayName("GET /post/{id} : 미인증 유저도 요청 가능 - 200 ")
  @WithAnonymousUser
  public void getPostDetail() throws Exception {
    // given , when
    when(accountService.getAccount("testUser")).thenReturn(null);
    when(postReadService.readPost(1, null)).thenReturn(
            dummyResponse()
    );


    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/post/1")

    ).andDo(print());

    // then

    result.andExpect(status().isOk())
            .andExpect(view().name("post_detail"))
            .andExpect(model().attributeExists("response"))
            .andExpect(model().attributeExists("comments"));
  }


  PostDetailResponse dummyResponse(){
    PostDetailResponse response = new PostDetailResponse();
    response.id = 1;
    response.boardID = 1;
    response.authorID = 1L;
    response.authorName = "author";
    response.title = "hello world";
    response.content = "blah blah";
    response.createDate = LocalDateTime.now();
    response.modifyDate = LocalDateTime.now();
    response.likeCount = 0L;
    response.viewCount = 0L;


    return response;
  }



}