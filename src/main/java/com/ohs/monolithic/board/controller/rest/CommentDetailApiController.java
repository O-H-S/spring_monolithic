package com.ohs.monolithic.board.controller.rest;

import com.ohs.monolithic.board.dto.CommentDeleteResponse;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.board.service.CommentLikeService;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/{commentID}")
public class CommentDetailApiController {

  final private AccountService accountService;
  final private CommentService commentService;
  final private CommentLikeService commentLikeService;


  @GetMapping
  public ResponseEntity<?> getComment(@AuthenticationPrincipal AppUser user, @PathVariable("commentID") Long id) {


    Long accountId = user!=null ? user.getAccountId() : null;
    CommentPaginationDto response = commentService.getCommentBy(id, accountId);

    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  // 통합 테스트 존재
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/commentLikes")
  public ResponseEntity<?> likeComment(@AuthenticationPrincipal AppUser user, @PathVariable("commentID") Long commentID) {

    Pair<Boolean, Long> result = commentLikeService.likeCommentEx(commentID, user.getAccountId());
    Long count = result.getFirst() ? result.getSecond()+1L :result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

  // 통합 테스트 존재
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/commentLikes")
  public ResponseEntity<?> unlikeComment(@AuthenticationPrincipal AppUser user, @PathVariable("commentID") Long commentID) {

    Pair<Boolean, Long> result = commentLikeService.unlikeCommentEx(commentID, user.getAccountId());
    Long count = result.getFirst() ? result.getSecond()-1L :result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResponseEntity<?> deleteComment(@AuthenticationPrincipal AppUser user, @PathVariable("commentID") Long commentId) {


    CommentDeleteResponse response = commentService.deleteCommentBy(commentId, user);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping
  public ResponseEntity<?> updateCommentContent(@AuthenticationPrincipal AppUser user, @PathVariable("commentID") Long commentId, @RequestBody @Valid CommentForm commentForm) {

    commentService.modifyCommentBy(commentId, user, commentForm);
    return ResponseEntity.status(HttpStatus.OK).build();
  }


  /*@PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.cService.getComment(id);
        Account siteUser = this.accountService.getAccount(principal.getName());
        this.cService.vote(comment, siteUser);
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }*/



}