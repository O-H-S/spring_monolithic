package com.ohs.monolithic.problem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProblemPaginationResponse {
  Integer totalPages;
  Long totalCounts;
  List<ProblemPaginationDto> data;
}
