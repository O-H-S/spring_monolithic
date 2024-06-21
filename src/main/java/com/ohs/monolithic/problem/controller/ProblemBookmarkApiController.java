package com.ohs.monolithic.problem.controller;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.problem.dto.ProblemBookmarkCreationForm;
import com.ohs.monolithic.problem.dto.ProblemBookmarkMutationResponse;
import com.ohs.monolithic.problem.dto.ProblemPostCreationResponse;
import com.ohs.monolithic.problem.service.ProblemBookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems/{id}/bookmarks")
public class ProblemBookmarkApiController {
  final private ProblemBookmarkService problemBookmarkService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping
  public ResponseEntity<?> createProblemBookmark(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id, @RequestBody ProblemBookmarkCreationForm bookmarkForm){

    ProblemBookmarkMutationResponse response = problemBookmarkService.bookmarkProblem(user, id, bookmarkForm);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping
  public ResponseEntity<?> deleteProblemBookmark(@AuthenticationPrincipal AppUser user, @PathVariable("id") Long id){

    ProblemBookmarkMutationResponse response = problemBookmarkService.unbookmarkProblem(user, id);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

}
