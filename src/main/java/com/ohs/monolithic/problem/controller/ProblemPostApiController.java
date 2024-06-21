package com.ohs.monolithic.problem.controller;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.problem.dto.ProblemPostCreationResponse;
import com.ohs.monolithic.problem.dto.ProblemPostPaginationResponse;
import com.ohs.monolithic.problem.service.ProblemPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems/{id}/posts")
public class ProblemPostApiController {
  final private ProblemPostService problemPostService;

  @GetMapping
  public ResponseEntity<?> getProblemPosts(@PathVariable("id") Long id, @RequestParam(name="page", defaultValue = "0") Integer page, @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize ){
    ProblemPostPaginationResponse response = problemPostService.getProblemPosts(id, page, pageSize);

    return ResponseEntity.ok(response);
  }
  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<?> createProblemPost(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id, @RequestBody @Valid PostForm postForm){

    ProblemPostCreationResponse response = problemPostService.createProblemPost(id, postForm, user);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
