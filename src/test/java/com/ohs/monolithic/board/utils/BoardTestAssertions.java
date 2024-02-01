package com.ohs.monolithic.board.utils;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostPaginationDto;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTestAssertions {
  public static void assertPostPaginationDto(Post originalPost, PostPaginationDto targetDto){

    assertThat(targetDto).isNotNull();
    assertThat(targetDto.getId()).isEqualTo(originalPost.getId());

  }

}
