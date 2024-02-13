package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.utils.BoardIntegrationTestHelper;
import com.ohs.monolithic.board.utils.IntegrationTestBase;
import com.ohs.monolithic.user.domain.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


public class PostWriteServiceIntegrationTest extends IntegrationTestBase {

  @Autowired
  private PostWriteService postWriteService;

  @Test
  @DisplayName("creatAllAsync() : ")
  void createAllAsync_0(){
    Triple<BoardResponse, Account, Post> givens = helper.InitDummy_BoardAccountPost();
    BoardResponse targetBoard = givens.a;
    Account writer = givens.b;

    LocalDateTime nowTime = LocalDateTime.now();
    long startTime = System.currentTimeMillis();
    long counts = 100;

    var future = postWriteService.createAllAsync(targetBoard.getId(), writer.getId(), (post, idx) -> {
      post.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx + 1, counts, System.currentTimeMillis() - startTime));
      post.setCreateDate(nowTime.plusNanos(idx * 1000));
    }, counts);


    future.join();

    assertThat(helper.boardService.getPostCount(targetBoard.getId())).isEqualTo(1 + counts);


  }

}