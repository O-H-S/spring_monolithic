package com.ohs.monolithic.board.service;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.board.domain.constants.PostTagType;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.repository.PostViewRepository;
import com.ohs.monolithic.utils.IntegrationTestWithMySQL;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.ArrayList;
import java.util.List;

public class PostReadServicePerformanceTest extends IntegrationTestWithMySQL {

  @Autowired
  private PostReadService postReadService;

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.jpa.show-sql", () -> "false");
    registry.add("logging.level.org.hibernate.SQL", () -> "OFF");
    registry.add("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", () -> "OFF");
  }

  int iterationCount = 1;
  int requestCount = 100;
  int tagCount = 25;
  List<Long> times = new ArrayList<>(iterationCount);
  long rtt = 1; // ms

  @Test
  @DisplayName("getPostReadOnly 성능테스트 - N+1 버전")
  public void getPostReadOnly_Inefficiency(){
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    PostDetailResponse targetPost = givens.c;
    List<String> tagNames = new ArrayList<>(tagCount);
    List<PostTagType> tagTypes = new ArrayList<>(tagCount);
    for(int i = 0; i < tagCount ; i++){
      tagNames.add("tag" + i);
      tagTypes.add(PostTagType.Normal);

    }
    if(tagCount > 0)
      helper.postTagService.addPostTagList(targetPost.getId(), tagNames, tagTypes, true);
    Account viewer = givens.b;

    // when
    System.out.println("=====================<read start>");
    long duration = 0;
    for(int i = 0; i < iterationCount; i++ ) {
      long startTime = System.nanoTime(); // 시작 시간 기록

      for (int j = 0; j < requestCount; j++) {
        PostDetailResponse response = postReadService.getPostReadOnly_Legacy(targetPost.getId(), viewer.getId(), false, false);
      }

      long endTime = System.nanoTime(); // 종료 시간 기록
      long d = (endTime - startTime) + (rtt * (tagCount + 1+1)*requestCount * 1_000_000L);
      duration += d;
      times.add(d/1_000_000);

    }
    System.out.println("요청 %d개, 태그 %d개".formatted(requestCount,tagCount));
    System.out.println(times.toString());
    System.out.println("평균 : " +duration/iterationCount /1_000_000 + "ms");
    System.out.println("======================<read end>");

  }


  @Test
  @DisplayName("getPostReadOnly 성능테스트 - 최적화 버전")
  public void getPostReadOnly_Optimal(){
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    PostDetailResponse targetPost = givens.c;
    List<String> tagNames = new ArrayList<>(tagCount);
    List<PostTagType> tagTypes = new ArrayList<>(tagCount);
    for(int i = 0; i < tagCount ; i++){
      tagNames.add("tag" + i);
      tagTypes.add(PostTagType.Normal);

    }
    if(tagCount > 0)
      helper.postTagService.addPostTagList(targetPost.getId(), tagNames, tagTypes, true);
    Account viewer = givens.b;

    System.out.println("=====================<read start>");
    long duration = 0;
    for(int i = 0; i < iterationCount; i++ ) {
      long startTime = System.nanoTime(); // 시작 시간 기록

      for (int j = 0; j < requestCount; j++) {
        PostDetailResponse response = postReadService.getPostReadOnly(targetPost.getId(), viewer.getId(), false, false);
      }


      long endTime = System.nanoTime(); // 종료 시간 기록

      long d = (endTime - startTime) + (rtt *  requestCount  * 1_000_000L);
      duration += d;
      times.add(d/1_000_000);

    }
    System.out.println("요청 %d개, 태그 %d개".formatted(requestCount,tagCount));
    System.out.println(times.toString());
    System.out.println("평균 : " +duration/iterationCount /1_000_000 + "ms");
    System.out.println("======================<read end>");

  }

}
