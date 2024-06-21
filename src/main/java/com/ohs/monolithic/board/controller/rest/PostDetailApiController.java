package com.ohs.monolithic.board.controller.rest;

import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.service.PostLikeService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.auth.domain.AppUser;
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
@RequestMapping("/api/posts/{id}")
public class PostDetailApiController {

  final private PostWriteService writeService;
  final private PostReadService readService;
  final private PostLikeService postLikeService;
  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResponseEntity<?> deletePost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id) throws Exception {


    writeService.deleteBy(id, user.getAccountId());
    return ResponseEntity.status(HttpStatus.OK).build();

  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping
  public ResponseEntity<?> updatePost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id, @RequestBody @Valid PostForm postForm) {


    writeService.modifyBy(id, user, postForm);
    Long accountId = user!=null ? user.getAccountId() : null;
    PostDetailResponse response = readService.readPost(id, accountId);
    return ResponseEntity.status(HttpStatus.OK).body(response);

  }

  @GetMapping
  public ResponseEntity<?> getPost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id) {


    Long accountId = user!=null ? user.getAccountId() : null;
    PostDetailResponse response = readService.readPost(id, accountId);

    return ResponseEntity.status(HttpStatus.OK).body(response);

  }


  @PreAuthorize("isAuthenticated()")
  @PostMapping("/postLikes")
  public ResponseEntity<?> likePost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id) {


    Pair<Boolean, Long> result = postLikeService.likePostEx(id, user);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(
            Map.of("id", id,
                    "changed", result.getFirst(),
                    "count", count
            )
    );
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/postLikes")
  public ResponseEntity<?> unlikePost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id) {


    Pair<Boolean, Long> result = postLikeService.unlikePostEx(id, user);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(
            Map.of("id", id,
                    "changed", result.getFirst(),
            "count", count
            )
    );
  }

}
