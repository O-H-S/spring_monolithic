package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardQueryForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
  final private BoardService boardService;

  // Test exists
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  @ResponseBody
  public ResponseEntity<?> createBoard(@Valid @RequestBody BoardCreationForm form, BindingResult bindingResult) {

    BoardResponse board = boardService.createBoard(form.getTitle(), form.getDesc());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", board.getId())); // 상태 코드 201과 함께 생성된 자원의 ID 반환

  }

  // Test exists
  @GetMapping
  public ResponseEntity<List<BoardResponse>> getAllBoards(@Valid @ModelAttribute BoardQueryForm form, BindingResult bindingResult) {

    List<BoardResponse> boards = boardService.getBoardsReadOnly(form.getIncludesTitle(), form.getIncludesDesc());

    return ResponseEntity.ok(boards); // 상태 코드 200과 함께 모든 게시판 목록 반환
  }

  // Test exists
  @GetMapping("/{id}")
  public ResponseEntity<BoardResponse> getBoard(@PathVariable("id") Integer id) {

    BoardResponse target = boardService.getBoardReadOnly(id);
    return ResponseEntity.ok(target);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<BoardResponse> deleteBoard(@PathVariable("id") Integer id) {
    BoardResponse target = BoardResponse.builder()
                    .id(id).build();
    boardService.deleteBoard(id);
    return ResponseEntity.ok(target);
  }

}
