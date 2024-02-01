package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.service.PostLikeService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{id}")
public class PostDetailApiController {

  final private AccountService accountService;
  final private PostWriteService writeService;
  final private PostLikeService postLikeService;
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResponseEntity<?> deletePost(Principal currentUser, @PathVariable("id") Long id) {

    Account operator = accountService.getAccount(currentUser.getName());
    writeService.deleteBy(id, operator);
    return ResponseEntity.status(HttpStatus.OK).build();

  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping
  public ResponseEntity<?> updatePost(Principal currentUser, @PathVariable("id") Long id, @RequestBody @Valid PostForm postForm) {

    Account operator = accountService.getAccount(currentUser.getName());
    writeService.modifyBy(id, operator, postForm);
    return ResponseEntity.status(HttpStatus.OK).build();

  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping("/postLikes")
  public ResponseEntity<?> likePost(Principal currentUser, @PathVariable("id") Long id) {

    Account voter = accountService.getAccount(currentUser.getName());
    Pair<Boolean, Long> result = postLikeService.likePostEx(id, voter);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/postLikes")
  public ResponseEntity<?> unlikePost(Principal currentUser, @PathVariable("id") Long id) {

    Account voter = accountService.getAccount(currentUser.getName());
    Pair<Boolean, Long> result = postLikeService.unlikePostEx(id, voter);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

}
