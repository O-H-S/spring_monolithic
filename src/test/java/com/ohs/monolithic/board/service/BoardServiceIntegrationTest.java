package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.utils.WithMockCustomUserContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class BoardServiceIntegrationTest extends IntegrationTestWithH2 {

  @Autowired
  private BoardService boardService;
  @Test
  @DisplayName("deleteBoard(Integer) : 정상적으로 삭제됨. ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
  public void deleteBoard() throws Exception {
    //given
    BoardResponse deletedBoard = boardService.createBoard("자유", "자유 게시판 설명");
    boardService.createBoard("건의", "건의 게시판 설명");

    List<BoardResponse> prevBoards = boardService.getBoardsReadOnly();


    // when
    boardService.deleteBoard(deletedBoard.getId(), WithMockCustomUserContext.getAppUser());

    // then
    List<BoardResponse> boards = boardService.getBoardsReadOnly();
    assertThat(boards).hasSize(prevBoards.size()-1);

  }

  @Test
  @DisplayName("getBoard(Integer)")
  @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
  public void getBoard() throws Exception {
    //given
    BoardResponse targetBoard = boardService.createBoard("자유", "자유 게시판 설명");
    boardService.createBoard("건의", "건의 게시판 설명");


    // when
    BoardResponse response = boardService.getBoardReadOnly(targetBoard.getId(), WithMockCustomUserContext.getAppUser());
    BoardResponse response2 = boardService.getBoardReadOnly(targetBoard.getId(), WithMockCustomUserContext.getAppUser());
    BoardResponse response3 = boardService.getBoardReadOnly(targetBoard.getId(), WithMockCustomUserContext.getAppUser());




  }


}
