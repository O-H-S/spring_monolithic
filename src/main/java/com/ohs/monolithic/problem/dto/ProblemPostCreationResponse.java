package com.ohs.monolithic.problem.dto;


import com.ohs.monolithic.board.dto.PostDetailResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProblemPostCreationResponse {
  PostDetailResponse postData;
  ProblemPaginationDto problemData;
}
