package com.ohs.monolithic.board.controller.rest;

import com.ohs.monolithic.board.dto.*;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.auth.domain.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    CommentCreationResponse result = commentService.createByID(postId, commentForm.getContent(), user.getAccountId());

    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping
  public ResponseEntity<?> getComments(@AuthenticationPrincipal AppUser user, @PathVariable("postID") Long postId, @RequestParam(name="page", defaultValue = "0") Integer page, @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize ){
    CommentPaginationResponse response = commentService.getComments(postId, user);

    return ResponseEntity.ok(response);
  }



}
