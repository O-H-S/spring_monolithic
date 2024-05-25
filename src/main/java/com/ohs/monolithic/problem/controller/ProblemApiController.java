package com.ohs.monolithic.problem.controller;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.dto.ProblemPaginationResponse;
import com.ohs.monolithic.problem.service.ProblemPaginationService;
import com.ohs.monolithic.problem.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemApiController {
  final private ProblemPaginationService problemPaginationService;
  final private ProblemService problemService;

  @GetMapping("/")
  public ResponseEntity<?> getProblems(@AuthenticationPrincipal AppUser user, @RequestParam(name="page", defaultValue = "0") Integer page, @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize ){

    Page<ProblemPaginationDto> result = problemPaginationService.getList(page, pageSize, user);

    ProblemPaginationResponse response = new ProblemPaginationResponse();
    response.setData(result.getContent());
    response.setTotalCounts(result.getTotalElements());
    response.setTotalPages(result.getTotalPages());

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getProblemData(@AuthenticationPrincipal AppUser user, @PathVariable Long id){

    ProblemPaginationDto result = problemService.getProblemReadOnly(id);
    return ResponseEntity.ok().body(result);
  }


}
