package com.ohs.monolithic.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentCreationResponse {
  Long commentCount;
  CommentPaginationDto commentData;
}
