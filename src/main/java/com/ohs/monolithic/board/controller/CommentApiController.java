package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.*;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.user.dto.AppUser;
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
@RequestMapping("/api/{postID}/comments")
public class CommentApiController {
  final private CommentService commentService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<?> createComment(@AuthenticationPrincipal AppUser user,
                                      @PathVariable("postID") Long postId,
                                      @RequestBody @Valid CommentForm commentForm) {
    Comment result = commentService.createByID(postId, commentForm.getContent(), user.getAccountId());

    CommentCreationResponse response = new CommentCreationResponse(result.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }




}
