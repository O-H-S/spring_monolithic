package com.ohs.monolithic.board.integration;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Tag("base")
@Tag("integrate")
public class BoardServiceIntegrationTest {

  @Autowired
  private BoardService boardService;
  @Autowired
  private BoardRepository boardRepository;

  @Test
  @DisplayName("deleteBoard(Integer) : 정상적으로 삭제됨. ")
  public void deleteBoard() throws Exception {
    //given
    BoardResponse deletedBoard = boardService.createBoard("자유", "자유 게시판 설명");
    boardService.createBoard("건의", "건의 게시판 설명");


    // when

    boardService.deleteBoard(deletedBoard.getId());

    // then
    List<BoardResponse> boards = boardService.getBoardsReadOnly();
    assertThat(boards).hasSize(1);


  }


}
