package com.ohs.monolithic.board.event;

import com.ohs.monolithic.board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PostCreateEvent {
  Post post;
}
