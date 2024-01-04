package com.ohs.monolithic.board.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.exception.BoardNotFoundException;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostReadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@ExtendWith(MockitoExtension.class)

@AutoConfigureRestDocs
@WebMvcTest(BoardController.class)
@EnableMethodSecurity(prePostEnabled = true)
@Tag("base")
@Tag("unit")
public class BoardControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService bService;

    @MockBean
    private PostReadService pService;
    private Gson gson; // json 직렬화,역직렬화

    @BeforeEach
    public void init() {
        gson = new Gson();
        /*mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();*/

    }


    /*================================================================================
    게시판 생성
    ================================================================================*/
    @Test
    @DisplayName("GET /board/create : 일반 유저는 게시판 생성 불가 - 403 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void createBoard_0() throws Exception {
        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/board/create")
                        .param("title", "abc")
                        .param("desc", "dc")
        );

        // then
        result.andExpect(status().isForbidden());
        // AccessDeniedException은 일반적으로 HTTP 403 Forbidden 상태 코드로 변환
    }

    @Test
    @DisplayName("GET /board/create : title, desc가 제공되지 않아도, 공백으로 생성 가능. 메인 페이지로 리다이렉트 - 3xx ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void createBoard_1() throws Exception {



        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/board/create")
                        //.param("title", "abc")
                        //.param("desc", "dc")
        );
        // then
        result.andExpect(status().is3xxRedirection()).andDo(print());
    }


    /*================================================================================
    게시판 이름 변경
    ================================================================================*/


    @Test
    @DisplayName("POST /board/{id}/title : 일반 유저는 게시판 이름 변경 불가 - 403 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void changeBoardTitle_0() throws Exception {
        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
        );

        // then
        result.andExpect(status().isForbidden());
        // AccessDeniedException은 일반적으로 HTTP 403 Forbidden 상태 코드로 변환
    }

    @Test
    @DisplayName("POST /board/{id}/title : 익명의 유저는 게시판 이름 변경 불가, 인증 페이지로 리다이렉트 - 302")
    @WithAnonymousUser
    public void changeBoardTitle_00() throws Exception {
        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
                        .with(csrf())
        );

        // then
        result.andExpect(status().isFound());
    }


    @Test
    @DisplayName("POST /board/{id}/title : csrf 토큰 미포함 - 403 ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void changeBoardTitle_2() throws Exception {

        when(bService.getBoard(1)).thenThrow(BoardNotFoundException.class);

        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
                        .param("boardTitle", "새로운 게시판 제목")
        );

        // then
        result.andExpect(status().isForbidden());
        // AccessDeniedException은 일반적으로 HTTP 403 Forbidden 상태 코드로 변환
    }

    @Test
    @DisplayName("POST /board/{id}/title : 존재하지 않는 게시판 ID - 404 ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void changeBoardTitle_3() throws Exception {

        // given
        when(bService.getBoard(1)).thenThrow(BoardNotFoundException.class);

        //, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
                        .with(csrf())
                        .param("boardTitle", "새로운 게시판 제목")
        );

        // then
        result.andExpect(status().isNotFound());
        // AccessDeniedException은 일반적으로 HTTP 403 Forbidden 상태 코드로 변환
    }

    @Test
    @DisplayName("POST /board/{id}/title : boardTitle이 없으면 실패 - 400 ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void changeBoardTitle_4() throws Exception {

        // given

        //, when
        String changedTitle = "새로운 게시판 제목";
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
                        .with(csrf())
        );

        // then
        result.andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("POST /board/{id}/title : 성공적으로 변경 되면, 홈으로 리다이렉트 - 302 ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void changeBoardTitle_5() throws Exception {


        //Board testBoard = BoardTestUtils.createBoardSimple(1, "test");
        Board testBoard = mock(Board.class);
        // given
        /*when(bService.getBoard(1)).thenThrow(BoardException.class);*/
        when(bService.getBoard(1)).thenReturn(testBoard);
        //when(bService.save(testBoard)).

        //, when
        String changedTitle = "새로운 게시판 제목";
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/board/1/title")
                        .with(csrf())
                        .param("boardTitle", changedTitle)
        );

        // then
        result.andExpect(status().isFound());
        verify(testBoard).setTitle(changedTitle);
    }

}
