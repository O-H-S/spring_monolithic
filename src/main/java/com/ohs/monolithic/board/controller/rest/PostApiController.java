package com.ohs.monolithic.board.controller.rest;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.*;
import com.ohs.monolithic.board.service.*;
import com.ohs.monolithic.auth.domain.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/{boardId}/posts")
public class PostApiController {

  final private BoardService boardService;
  final private PostWriteService writeService;
  final private BoardAliasService boardAliasService;
  final private PostPaginationService postPaginationService;




  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<?> createPost(@AuthenticationPrincipal AppUser user,
                                      @PathVariable("boardId") String boardId,
                                      @RequestBody @Valid PostForm postForm,
                                      @RequestParam(value = "includeData", defaultValue = "true",  required = false) Boolean includeData) {
    PostDetailResponse result = writeService.create(boardAliasService.tryGetBoardId(boardId), postForm, user);
    if(!includeData)
      return ResponseEntity.status(HttpStatus.OK).build();

    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @GetMapping
  public ResponseEntity<?> getPosts(@AuthenticationPrincipal AppUser user, @PathVariable("boardId") String boardId, @RequestParam(name="page", defaultValue = "0") Integer page, @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize ){

    Page<PostPaginationDto> result = postPaginationService.getListWithCovering(page, boardAliasService.tryGetBoardId(boardId), pageSize);

    PostPaginationResponse response = new PostPaginationResponse();
    response.setData(result.getContent());
    response.setTotalCounts(result.getTotalElements());
    response.setTotalPages((long)result.getTotalPages());


    return ResponseEntity.ok(response);
  }






  @GetMapping(params = "lastPostId")
  public ResponseEntity<?> getPostByScroll(@PathVariable("boardId") Integer boardId, @RequestParam(value="lastPostId") Long lastId){

    List<PostPaginationDto> scroll = this.postPaginationService.getPostListAsScroll(boardId, lastId, 10);

    return ResponseEntity.status(HttpStatus.OK).body(scroll);
  }





  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/bulk")
  public ResponseEntity<?> bulkInsertPosts(@AuthenticationPrincipal AppUser user, @Valid @RequestBody BulkInsertForm form, @PathVariable("boardId") Integer boardId/* BindingResult bindingResult*/) {





    // 리팩토링 필요.
    // 컨트롤러에서 도메인 객체를 다루고 있음. (PostProxy or PostManipulator)
    LocalDateTime nowTime = LocalDateTime.now();
    long startTime = System.currentTimeMillis();

    writeService.createAllAsync(boardId, user.getAccountId(), (post, idx) -> {
      post.setTitle(form.getTitle());
      post.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx + 1, form.getCount(), System.currentTimeMillis() - startTime));
      post.setCreateDate(nowTime.plusNanos(idx * 1000));
    }, form.getCount());

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }



}
