package com.ohs.monolithic.problem.controller;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.dto.ProblemPaginationResponse;
import com.ohs.monolithic.problem.service.ProblemPaginationService;
import com.ohs.monolithic.problem.service.ProblemService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
@Validated
public class ProblemApiController {
  final private ProblemPaginationService problemPaginationService;
  final private ProblemService problemService;

  @GetMapping("/")
  public ResponseEntity<?> getProblems(@AuthenticationPrincipal AppUser user,
                                       @RequestParam(name="page", defaultValue = "0") @Min(0) Integer page,
                                       @RequestParam(name="pageSize", defaultValue = "10") @Min(5) @Max(100) Integer pageSize,
                                       @RequestParam(name="platforms", required = false) List<String> platforms,
                                       @RequestParam(name="sort", required = false) String sort,
                                       @RequestParam(name="minLevel", required = false) Float minLevel,
                                       @RequestParam(name="maxLevel", required = false) Float maxLevel,
                                       @RequestParam(name="isDescending", required = false, defaultValue = "true") Boolean isDescending,
                                       @RequestParam(name="keywords", required = false) @Size(max = 255) String keywords
                                       ){


    ProblemPaginationService.PaginationOption option = ProblemPaginationService.PaginationOption.builder()
            .isDescending(isDescending)
            .keywords(keywords)
            .platforms(platforms)
            .sortColumn(sort)
            .levelRange(new Float[]{minLevel, maxLevel})
            .build();

    Page<ProblemPaginationDto> result = problemPaginationService.getListWithOption(page, pageSize, option, user);

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
