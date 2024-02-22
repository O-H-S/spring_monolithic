package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.BoardPaginationType;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.utils.IntegrationTestBase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostPaginationServiceIntegrationTest extends IntegrationTestBase {

  @Autowired
  private PostPaginationService postPaginationService;

  static int pageSize = 10;
  private static Stream<Arguments> parameters_getPostListAsPage() {
    /*
      Arguments.of(BoardPaginationType.Offset, 30, 1, 10)
     BoardPaginationType.Offset 방식의 게시판에 30개의 Post가 존재할 때, 페이지 1를 읽으면, 10개의 Post가 나와야한다.
     */

    List<TestParams> testParamsList = Arrays.asList(
            TestParams.of(BoardPaginationType.Offset, 30, 1, 10),
            TestParams.of(BoardPaginationType.Offset, 6, 0, 6),
            TestParams.of(BoardPaginationType.Offset, 16, 1, 6),
            TestParams.of(BoardPaginationType.Offset, 16, 2, 0),
            TestParams.of(BoardPaginationType.Offset, 0, 0, 0),

            TestParams.of(BoardPaginationType.Offset_CountCache, 30, 1, 10),
            TestParams.of(BoardPaginationType.Offset_CountCache, 6, 0, 6),
            TestParams.of(BoardPaginationType.Offset_CountCache, 16, 1, 6),
            TestParams.of(BoardPaginationType.Offset_CountCache, 16, 2, 0),
            TestParams.of(BoardPaginationType.Offset_CountCache, 0, 0, 0),

            TestParams.of(BoardPaginationType.Offset_CountCache_CoveringIndex, 30, 1, 10),
            TestParams.of(BoardPaginationType.Offset_CountCache_CoveringIndex, 6, 0, 6),
            TestParams.of(BoardPaginationType.Offset_CountCache_CoveringIndex, 16, 1, 6),
            TestParams.of(BoardPaginationType.Offset_CountCache_CoveringIndex, 16, 2, 0),
            TestParams.of(BoardPaginationType.Offset_CountCache_CoveringIndex, 0, 0, 0),


            // Cursor 방식인 게시판에 page 형식으로 조회하면, 예외 발생
            TestParams.of(BoardPaginationType.Cursor, 30, 1, (Integer) null),
            TestParams.of(BoardPaginationType.Cursor, 6, 0, (Integer) null),
            TestParams.of(BoardPaginationType.Cursor, 16, 1,(Integer) null),
            TestParams.of(BoardPaginationType.Cursor, 16, 2,(Integer) null),
            TestParams.of(BoardPaginationType.Cursor, 0, 2, (Integer) null)
    );

    return testParamsList.stream().map(Arguments::of);
  }

  @ParameterizedTest
  @MethodSource("parameters_getPostListAsPage")
  @DisplayName("getPostListAsPage(boardId, page, count)")
  public void getPostListAsPage(TestParams testParams){
    Integer boardId = helper.boardService.createBoard("", "", testParams.paginationType).getId(); // dummy board 생성
    List<Post> posts = helper.simplePost(boardId, null, testParams.totalPosts); // dummy posts 생성

    // 예외 발생을 기대할 때
    if (testParams.expectedException != null){
      assertThrows(testParams.expectedException, () -> {
        postPaginationService.getPostListAsPage(boardId, testParams.targetPage, pageSize);
      });
      return;
    }

    // 정상적인 출력을 기대할 때.
    Page<PostPaginationDto> results = postPaginationService.getPostListAsPage(boardId, testParams.targetPage, pageSize);

    if(testParams.expectedPostCount != null) {
      results.forEach(dto -> System.out.println(dto.toString()));
      assertThat(results).hasSize(testParams.expectedPostCount);
    }
    else{
      assertThat(results).isNull();
    }
  }

  private static class TestParams {
    final BoardPaginationType paginationType;
    final int totalPosts;
    final int targetPage;
    final Integer expectedPostCount;
    final Class<? extends Throwable> expectedException; // 예상되는 에러 클래스 타입

    private TestParams(BoardPaginationType paginationType, int totalPosts, int targetPage, Integer expectedPostCount, Class<? extends Throwable> expectedException) {
      this.paginationType = paginationType;
      this.totalPosts = totalPosts;
      this.targetPage = targetPage;
      this.expectedPostCount = expectedPostCount;
      this.expectedException = expectedException;
    }

    static TestParams of(BoardPaginationType paginationType, int totalPosts, int targetPage, Integer expectedPostCount) {
      return new TestParams(paginationType, totalPosts, targetPage, expectedPostCount, null);
    }

    static TestParams of(BoardPaginationType paginationType, int totalPosts, int targetPage,  Class<? extends Throwable> expectedException) {
      return new TestParams(paginationType, totalPosts, targetPage, null, expectedException);
    }
  }




}
