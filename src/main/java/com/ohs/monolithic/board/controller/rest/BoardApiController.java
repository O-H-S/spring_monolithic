package com.ohs.monolithic.board.controller.rest;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardQueryForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.service.BoardAliasService;
import com.ohs.monolithic.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardApiController {
  final private BoardService boardService;
  final private BoardAliasService boardAliasService;
  // Test exists
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  @ResponseBody
  public ResponseEntity<?> createBoard(@Valid @RequestBody BoardCreationForm form, BindingResult bindingResult) {

    BoardResponse board = boardService.createBoard(form.getTitle(), form.getDesc());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", board.getId())); // 상태 코드 201과 함께 생성된 자원의 ID 반환

  }


  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<BoardResponse> updateBoard(@PathVariable("id") String id, @Valid @ModelAttribute BoardCreationForm form) {

    BoardResponse result = boardService.updateBoard(boardAliasService.tryGetBoardId(id), form);
    return ResponseEntity.ok(result);
  }


  // Test exists
  @GetMapping
  public ResponseEntity<List<BoardResponse>> getAllBoards(@Valid @ModelAttribute BoardQueryForm form, BindingResult bindingResult) {

    List<BoardResponse> boards = boardService.getBoardsReadOnly(form.getIncludesTitle(), form.getIncludesDesc());

    return ResponseEntity.ok(boards); // 상태 코드 200과 함께 모든 게시판 목록 반환
  }

  // Test exists
  @GetMapping("/{id}")
  public ResponseEntity<BoardResponse> getBoard(@AuthenticationPrincipal AppUser user,  @PathVariable("id") String id) {

    BoardResponse target = boardService.getBoardReadOnly(boardAliasService.tryGetBoardId(id), user);
    return ResponseEntity.ok(target);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<BoardResponse> deleteBoard(@PathVariable("id") String id) {
    Integer id_int = boardAliasService.tryGetBoardId(id);
    BoardResponse target = BoardResponse.builder()
            .id(id_int).build();
    boardService.deleteBoard(id_int);
    return ResponseEntity.ok(target);
  }


}
