package com.ohs.monolithic.board.service;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.dto.AccountResponse;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.utils.IntegrationTestWithMySQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class PostPaginationServicePerformanceTest extends IntegrationTestWithMySQL {

  @Autowired
  PostWriteService postWriteService;

  @Autowired
  PostPaginationService postPaginationService;

  long postCount = 100;

  @Test
  @DisplayName("legacy")

  public void test(){

    BoardResponse boardResponse = helper.simpleBoard();
    Account account = helper.simpleAccount();

    LocalDateTime nowTime = LocalDateTime.now();
    long startTime = System.currentTimeMillis();

    postWriteService.createAllAsync(boardResponse.getId(), account.getId(), (post, idx) -> {
      post.setTitle("test title");
      post.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx + 1, postCount, System.currentTimeMillis() - startTime));
      post.setCreateDate(nowTime.plusNanos(idx * 1000));
    },postCount).join();


    var result = postPaginationService.getList_Legacy(0, boardResponse.getId());
    System.out.println("finished");
  }

}
