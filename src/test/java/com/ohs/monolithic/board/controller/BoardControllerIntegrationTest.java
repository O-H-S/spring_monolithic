package com.ohs.monolithic.board.controller;


import com.nimbusds.jose.shaded.gson.JsonObject;
import com.ohs.monolithic.board.BoardPaginationType;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.WithMockCustomUser;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class BoardControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private BoardService boardService;
    @Test
    @DisplayName("POST /api/boards: desc가 생략된 요청도 가능하다 - 201  ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void createBoard_0() throws Exception {
        //given

        // when
        JsonObject formObject = new JsonObject();
        formObject.addProperty("title", "test title");

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/boards")
                        .with(csrf())
                        .content(formObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isCreated());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    @DisplayName("POST /api/boards: 성공 - 201  ")
    @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
    public void createBoard_1() throws Exception {
        //given

        // when
        BoardCreationForm form = new BoardCreationForm();
        form.setTitle("Test Title");
        form.setDesc("Test Description");
        form.setPaginationType(BoardPaginationType.Offset_CountCache_CoveringIndex);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/boards")
                        .with(csrf())
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)

        );

        // then
        result.andExpect(status().isCreated());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/create-succeeded-recommended",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }


    @Test
    @DisplayName("GET /api/boards: 전체 게시판 조회 - 200  ")
    public void getBoards_0() throws Exception {
        //given
        boardService.createBoard("자유", "자유 게시판 설명");
        boardService.createBoard("건의", "건의 게시판 설명");
        // when

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        // then
        result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/get-succeeded-recommended",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

    @Test
    @DisplayName("GET /api/boards/{id}: 특정 게시판 조회 - 200  ")
    public void getBoard_0() throws Exception {
        //given
        boardService.createBoard("자유", "자유 게시판 설명");
        BoardResponse target = boardService.createBoard("건의", "건의 게시판 설명");




        // when

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/boards/" + target.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        // then
        result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/{id}/get-succeeded-recommended",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }

    @Test
    @DisplayName("DELETE /api/boards/{id}: 특정 게시판 삭제 - 200  ")
    @WithMockUser(username = "hyeonsu", authorities = "ADMIN")
    public void deleteBoard_0() throws Exception {
        //given
        BoardResponse target = boardService.createBoard("자유", "자유 게시판 설명");
        boardService.createBoard("건의", "건의 게시판 설명");
        // when

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/boards/" + target.getId().toString())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print());

        // then

        result.andExpect(status().isOk());
        result.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        result.andDo(document("boards/{id}/delete-succeeded-recommended",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));

    }



}
