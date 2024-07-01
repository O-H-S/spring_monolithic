package com.ohs.monolithic.board.controller;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.SecurityConfigForUnitTest;
import com.ohs.monolithic.board.controller.rest.PostApiController;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.service.*;
import com.ohs.monolithic.common.exception.DataNotFoundException;
import com.ohs.monolithic.utils.ControllerTestBase;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.account.service.AccountService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostApiController.class)
public class PostApiControllerTest extends ControllerTestBase {


    @MockBean
    private PostWriteService writeService;

    @MockBean
    private BoardService boardService;

    @MockBean
    private BoardAliasService boardAliasService;

    @MockBean
    private PostPaginationService postPaginationService;



    private String url;


    /*================================================================================
        Bulk Insert
    ================================================================================*/


    @Test
    @DisplayName("POST /api/{board}/posts/bulk : form 누락이면 실패 - 400 ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
    public void bulkInsert_실패_form누락() throws Exception {

        // given, when
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/1/posts/bulk")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)

        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
        result.andDo(document("posts/{board}/bulk-failed-notform",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("POST /api/{board}/posts/bulk : 어드민이 아니면 실패 - 403 ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
    public void bulkInsert_실패_어드민아님() throws Exception {

        // given, when

        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/1/posts/bulk")
                        .with(csrf())
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)

        ).andDo(print());

        // then
        result.andExpect(status().isForbidden());
        result.andDo(document("posts/{board}/bulk-failed-permission",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("POST /api/{board}/posts/bulk  : form 검증 실패 - 400 ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
    public void bulkInsert_실패_FORM() throws Exception {

        // given, when

        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(null)
                .build();

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/1/posts/bulk")
                        .with(csrf())
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)

        ).andDo(print());

        // then
        result.andExpect(status().isBadRequest());
        result.andDo(document("posts/{board}/bulk-failed-notverified",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("POST /api/{board}/posts/bulk  : 게시판이 존재하지 않으면 실패- 404 ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
    public void bulkInsert_실패_게시판없음() throws Exception {

        // given, when
        /*UnnecessaryStubbingException은 Mockito에서 설정한 스텁(stub)이 테스트 중에 사용되지 않았을 때 발생하는 예외입니다.
        이 예외는 Mockito의 기본 엄격 모드(strictness)에서 발생하며, 테스트 코드에 불필요한 설정이 있음을 알려줍*/
        List<Integer> tryIDs = List.of(-1,0,2);
        for(Integer idx : tryIDs) {
            //doThrow(new BoardNotFoundException(idx, "Not Exist")).when(boardService).assertBoardExists(intThat(argument -> argument != 1));
            doThrow(new DataNotFoundException("board", "Not Exist")).when(boardService).assertBoardExists(idx);
        }
            //lenient().doThrow(true).when(boardService).assertBoardExists(1); // lenient : 엄격 모드(strictness)를 조절


        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();


        for(Integer idx : tryIDs) {
            // when
            url = String.format( "/api/%d/posts/bulk", idx);
            ResultActions result = mockMvc.perform(
                    MockMvcRequestBuilders
                            .post(url)
                            .with(csrf())
                            .content(gson.toJson(form))
                            .contentType(MediaType.APPLICATION_JSON)

            ).andDo(print());

            // then
            result.andExpect(status().isNotFound());
            result.andDo(document("posts/{board}/bulk-failed-nonboard",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())));
        }
    }


    @Test
    @DisplayName("POST /api/{board}/posts/bulk  : 성공- 201 ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
    public void bulkInsert_성공() throws Exception {

        doNothing().when(boardService).assertBoardExists(anyInt());
        //when(accountService.getAccount(anyString())).thenReturn(Account.builder().nickname("test").build());
        // given, when

        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/1/posts/bulk")
                        .with(csrf())
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)

        ).andDo(print());

        // then
        result.andExpect(status().isCreated());
        result.andDo(document("posts/{board}/bulk-succeeded-normal",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

    /*

    @Test
    public void bulkInsert_성공_100개() throws Exception {
        doReturn(true).when(boardService).isExist(1);
        doNothing().when(writeService).createAll(eq(1), anyList());

        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();

        // when
        ResultActions result = sendRequest(form);

        // then
        result.andExpect(status().isCreated());
        verify(writeService).createAll(eq(1), anyList());
    }*/


}