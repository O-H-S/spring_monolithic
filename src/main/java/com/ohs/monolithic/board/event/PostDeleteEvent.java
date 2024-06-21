package com.ohs.monolithic.board.event;


import com.ohs.monolithic.board.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class PostDeleteEvent {
  Post post;
}
