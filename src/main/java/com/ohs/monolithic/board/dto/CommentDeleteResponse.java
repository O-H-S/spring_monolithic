package com.ohs.monolithic.board.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CommentDeleteResponse {
  Long postId;
  Integer commentCount;
}
