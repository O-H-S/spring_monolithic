package com.ohs.monolithic.problem.dto;

import com.ohs.monolithic.board.dto.PostPaginationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ProblemPostPaginationResponse {
  Long totalPages;
  Long totalCounts;
  List<PostPaginationDto> data;
}
