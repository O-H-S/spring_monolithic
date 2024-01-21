package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardQueryForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.CommentService;
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
@RequestMapping("/api/posts/{postID}/comments")
public class CommentApiController {
  final private CommentService commentService;

  //
  //@PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public ResponseEntity<?> createComment(@PathVariable("postID") Integer postID, @Valid @RequestBody BoardCreationForm form, BindingResult bindingResult) {

    /*BoardResponse board = boardService.createBoard(form.getTitle(), form.getDesc());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", board.getId())); // 상태 코드 201과 함께 생성된 자원의 ID 반환*/
    return null;
  }


}
