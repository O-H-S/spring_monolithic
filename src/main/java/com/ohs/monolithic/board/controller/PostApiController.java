package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostLike;
import com.ohs.monolithic.board.dto.*;
import com.ohs.monolithic.board.service.*;
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
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{boardId}/posts")
public class PostApiController {

  final private BoardService boardService;
  final private PostWriteService writeService;
  final private AccountService accountService;
  final private PostPaginationService postPaginationService;



  // 리팩토링 필요, BoardId를 form안에 말고 path로 처리하기.
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


  @GetMapping(params = "lastPostId")
  public ResponseEntity<?> getPostByScroll(@PathVariable("boardId") Integer boardId, @RequestParam(value="lastPostId") Long lastId){

    List<PostPaginationDto> scroll = this.postPaginationService.getPostListAsScroll(boardId, lastId, 10);

    return ResponseEntity.status(HttpStatus.OK).body(scroll);
  }





  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/bulk")
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



}
