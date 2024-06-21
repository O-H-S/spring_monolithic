package com.ohs.monolithic.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostPaginationResponse {
  Long totalPages;
  Long totalCounts;
  List<PostPaginationDto> data;
}
