package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostLike;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostLikeService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController {

  final private BoardService boardService;
  final private PostWriteService writeService;
  final private AccountService accountService;
  final private PostReadService readService;
  final private PostLikeService postLikeService;

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePost(Principal currentUser, @PathVariable("id") Long id) {

    Account operator = accountService.getAccount(currentUser.getName());
    writeService.deleteBy(id, operator);
    return ResponseEntity.status(HttpStatus.OK).build();

  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<?> createPost(Principal currentUser,
                                      @RequestBody @Valid PostForm postForm,
                                      @RequestParam(value = "includeData", defaultValue = "true",  required = false) Boolean includeData) {
    Account author = accountService.getAccount(currentUser.getName());
    Post result = writeService.create(postForm.getBoardId(), postForm, author);
    if(!includeData)
      return ResponseEntity.status(HttpStatus.OK).build();
    return ResponseEntity.status(HttpStatus.CREATED).body(PostDetailResponse.of(result, Boolean.TRUE, Boolean.FALSE));
  }


  @PreAuthorize("isAuthenticated()")
  @PutMapping("/{id}")
  public ResponseEntity<?> updatePost(Principal currentUser, @PathVariable("id") Long id, @RequestBody @Valid PostForm postForm) {

    Account operator = accountService.getAccount(currentUser.getName());
    writeService.modifyBy(id, operator, postForm);
    return ResponseEntity.status(HttpStatus.OK).build();

  }


  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/{boardId}/bulk")
  public ResponseEntity<?> bulkInsertPosts(Principal currentUser, @Valid @RequestBody BulkInsertForm form, @PathVariable("boardId") Integer boardId/* BindingResult bindingResult*/) {


    boardService.assertBoardExists(boardId);
    Account operator = accountService.getAccount(currentUser.getName());

    // 리팩토링 필요.
    // 컨트롤러에서 도메인 객체를 다루고 있음. (PostProxy or PostManipulator)
    LocalDateTime nowTime = LocalDateTime.now();
    long startTime = System.currentTimeMillis();

    writeService.createAllAsync(boardId, operator.getId(), (post, idx) -> {
      post.setTitle(form.getTitle());
      post.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx + 1, form.getCount(), System.currentTimeMillis() - startTime));
      post.setCreateDate(nowTime.plusNanos(idx * 1000));
    }, form.getCount());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{id}/postLikes")
  public ResponseEntity<?> likePost(Principal currentUser, @PathVariable("id") Long id) {

    Account voter = accountService.getAccount(currentUser.getName());
    Pair<Boolean, Long> result = postLikeService.likePostEx(id, voter);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{id}/postLikes")
  public ResponseEntity<?> unlikePost(Principal currentUser, @PathVariable("id") Long id) {

    Account voter = accountService.getAccount(currentUser.getName());
    Pair<Boolean, Long> result = postLikeService.unlikePostEx(id, voter);
    Long count = result.getSecond();
    return ResponseEntity.status(HttpStatus.OK).body(Map.of("changed", result.getFirst(),
            "count", count));
  }

}
