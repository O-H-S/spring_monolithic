package com.ohs.monolithic.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentPaginationDto {
  Long id;
  String content;
  Long writerId;
  String writerNickname;
  Long likeCount;

  @Setter
  Boolean liked;

  LocalDateTime createDate;
  LocalDateTime modifyDate;
}
