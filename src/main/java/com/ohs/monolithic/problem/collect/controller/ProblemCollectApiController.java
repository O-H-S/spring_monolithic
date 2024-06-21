package com.ohs.monolithic.problem.collect.controller;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.problem.collect.dto.CollectProgressResponse;
import com.ohs.monolithic.problem.collect.dto.CollectProgressUpdateForm;
import com.ohs.monolithic.problem.collect.service.ProblemCollectService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProblemCollectApiController {
  final ProblemCollectService problemCollectService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/api/collectProgresses")
  public ResponseEntity<?> getCollectProgress(@AuthenticationPrincipal AppUser user, @RequestParam(value = "target", required = true) String target, @RequestParam(value = "version", required = true) Integer version){

    CollectProgressResponse response = problemCollectService.getProgress(user, target, version);

    return ResponseEntity.ok().body(response);
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("/api/collectProgresses")
  public ResponseEntity<?> updateCollectProgress(@AuthenticationPrincipal AppUser user, @RequestParam(value = "target", required = true) String target, @RequestParam(value = "version", required = true) Integer version, @RequestBody CollectProgressUpdateForm form){

    problemCollectService.updateProgressWindow(user, target, version, form);

    return ResponseEntity.ok().build();
  }

}


