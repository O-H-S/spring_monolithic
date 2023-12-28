package com.ohs.monolithic.board.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.ohs.monolithic.SecurityConfigForUnitTest;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.exception.BoardNotFoundException;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureRestDocs
@WebMvcTest(BoardApiController.class)
@EnableMethodSecurity(prePostEnabled = true)
@Import(SecurityConfigForUnitTest.class)
//@Import({OAuth2LoginConfig.class, SecurityConfig.class})
//@ImportAutoConfiguration(exclude = {OAuth2ClientAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@Tag("base")
@Tag("unit")
class BoardApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService bService;

    private Gson gson; // json 직렬화,역직렬화



    @BeforeEach
    public void init() {

        gson = new Gson();

    }


    /*================================================================================
        게시판 생성
    ================================================================================*/

    @Test
    @DisplayName("POST /api/boards : 일반 유저는 게시판 생성 불가 - 403 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void createBoard_0() throws Exception {
        // given, when
        BoardCreationForm form = BoardCreationForm.builder()
                .title("Test Title")
                .desc("Test Description")
                .build();

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/boards")
                        .with(csrf())
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)

        ).andDo(print());

        // then
        result.andExpect(status().isForbidden());
        // AccessDeniedException은 일반적으로 HTTP 403 Forbidden 상태 코드로 변환

        result.andDo(document("boards/create-failed-permission",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("POST /api/boards : desc 누락 되어도 생성 가능 - 201 ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void createBoard_1() throws Exception {
        // given, when


        when(bService.createBoard(anyString(), anyString())).thenReturn(BoardResponse.builder().id(1).title("test title").build());

        JsonObject formObject = new JsonObject();
        formObject.addProperty("title", "test title");

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/boards")
                        .with(csrf())
                        .content(formObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        // then
        result.andExpect(status().isCreated());
        result.andDo(document("boards/create-succeeded-withoutparam",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }


     /*================================================================================
        전체 게시판 조회
    ================================================================================*/

    @Test
    @DisplayName("GET /api/boards : 입력데이터가 없어도 조회 가능 - 200 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void getBoards_0() throws Exception {
        // given, when
        List<BoardResponse> expectedBoards = Arrays.asList(
                BoardResponse.fromEntity(BoardTestUtils.createBoardSimple(1, "자유", "자유로운"), 0L),
                BoardResponse.fromEntity(BoardTestUtils.createBoardSimple(2, "건의", "건의하는"), 10L)
        );
        when(bService.getBoardsReadOnly(anyBoolean(), anyBoolean())).thenReturn(expectedBoards);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards")
        ).andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/get-succeeded-withoutparam",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

    @Test
    @DisplayName("GET /api/boards : 입력 데이터 일부 누락 되어도 가능 - 200 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void getBoards_1() throws Exception {
        // given, when
        List<BoardResponse> expectedBoards = Arrays.asList(
                BoardResponse.fromEntity(BoardTestUtils.createBoardSimple(1, "자유", "자유로운"), 0L),
                BoardResponse.fromEntity(BoardTestUtils.createBoardSimple(2, "건의", "건의하는"), 10L)
        );
        when(bService.getBoardsReadOnly(anyBoolean(), anyBoolean())).thenReturn(expectedBoards);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards")
                        .param("includesTitle", "true")
                        .param("includesDesc", "false")
        ).andDo(print());

        // then  
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/get-succeeded-withparam",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

    @Test
    @DisplayName("GET /api/boards : 게시판이 존재하지 않아도 성공 - 200 ")
    @WithMockUser(username = "hyeonsu", authorities = "USER")
    public void getBoards_2() throws Exception {
        // given, when
        List<BoardResponse> expectedBoards = new ArrayList<>();

        when(bService.getBoardsReadOnly(anyBoolean(), anyBoolean())).thenReturn(expectedBoards);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards")
                        .param("includesTitle", "true")
                        .param("includesDesc", "true")
                        .param("includesPostCounts", "true")
        ).andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/get-succeeded-emptyboards",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

     /*================================================================================
        특정 게시판 조회
    ================================================================================*/

    @Test
    @DisplayName("GET /api/boards/{id} : 해당 게시판이 존재하지 않으면 실패 - 404 ")
    public void getBoard_0() throws Exception {
        // given, when

        when(bService.getBoardReadOnly(anyInt())).thenThrow(new BoardNotFoundException(1, "Not exists"));

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards/1")
        ).andDo(print());

        // then
        result.andExpect(status().isNotFound());
        result.andDo(document("boards/{id}/get-failed-notfound",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

    @Test
    @DisplayName("GET /api/boards/{id} : 게시판 데이터를 리턴한다 - 200 ")
    public void getBoard_1() throws Exception {
        // given, when

        when(bService.getBoardReadOnly(anyInt())).thenReturn(BoardResponse.fromEntity( BoardTestUtils.createBoardSimple(1, "자유", "자유롭게 작성하시오."), 10L));

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards/1")
        ).andDo(print());

        // then
        result.andExpect(status().isOk());
        result.andDo(document("boards/{id}/get-succeed-found",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }


}