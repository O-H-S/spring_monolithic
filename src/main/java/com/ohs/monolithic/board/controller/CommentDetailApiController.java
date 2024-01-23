package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.service.CommentLikeService;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.Vector;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/{commentID}")
public class CommentDetailApiController {

  final private AccountService accountService;
  final private CommentService commentService;
  final private CommentLikeService commentLikeService;


  // 통합 테스트 존재
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/commentLikes")
  public ResponseEntity<?> likeComment(Principal currentUser, @PathVariable("commentID") Long commentID) {
 // @AuthenticationPrincipal UserDetails currentUser
    Account voter = accountService.getAccount(currentUser.getName());
    Comment target = commentService.getComment(commentID);

    Boolean changed = commentLikeService.likeComment(target, voter);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", changed));
  }

  // 통합 테스트 존재
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/commentLikes")
  public ResponseEntity<?> unlikeComment(Principal currentUser, @PathVariable("commentID") Long commentID) {
    // @AuthenticationPrincipal UserDetails currentUser
    Account voter = accountService.getAccount(currentUser.getName());
    Comment target = commentService.getComment(commentID);

    Boolean changed = commentLikeService.unlikeComment(target, voter);
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", changed));
  }




}