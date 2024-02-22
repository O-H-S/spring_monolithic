package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.repository.PostViewRepository;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.account.domain.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class PostReadServiceIntegrationTest extends IntegrationTestBase {

  @Autowired
  private PostReadService postReadService;

  @Autowired
  private PostViewRepository postViewRepository;

  @Test
  @DisplayName("readPost(Integer, Account): 게시글을 조회하면, viewCount가 증가하고, PostView 가 추가된다.")
  public void readPost(){
    Triple<BoardResponse, Account, Post> givens = helper.InitDummy_BoardAccountPost();
    Post targetPost = givens.c;
    Account viewer = givens.b;

    long oldCount = givens.c.getViewCount();

    // when

    PostDetailResponse response = postReadService.readPost(targetPost.getId(), viewer.getId());

    // then

    Post postInDB = helper.postReadService.getPost(targetPost.getId());
    assertThat(targetPost.getViewCount()).isEqualTo(oldCount); // 새로운 트랜잭션에서 viewCount를 증가시키므로, 기존 Post 엔티티의 viewCount는 그대로이다.
    assertThat(postInDB.getViewCount()).isEqualTo(oldCount + 1); // DB에는 증가가 적용되어 있으므로, 다시 불러오면 증가된 값이 나온다.
    assertThat(postViewRepository.findByPostIdAndUserId(targetPost.getId(), viewer.getId())).isNotNull(); // PostView가 추가되어 있어야한다.

    assertThat(targetPost.getId()).isEqualTo(response.id);
    assertThat(targetPost.getBoard().getId()).isEqualTo(response.boardID);
    assertThat(targetPost.getAuthor().getId()).isEqualTo(response.authorID);
    //assertThat(response.viewCount).isEqualTo(postInDB.getViewCount());
  }

  @Test
  @DisplayName("readPost(Integer, Account): 같은 사람이 여러번 조회하면, viewCount는 변화 없고 PostView의 count만 증가한다.")
  public void readPost_0(){
    Triple<BoardResponse, Account, Post> givens = helper.InitDummy_BoardAccountPost();
    Post targetPost = givens.c;
    Account viewer = givens.b;

    PostDetailResponse response = postReadService.readPost(targetPost.getId(), viewer.getId());

    long old_uniqueView = helper.postReadService.getPost(targetPost.getId()).getViewCount();
    int old_repeativeView = postViewRepository.findByPostIdAndUserId(targetPost.getId(), viewer.getId()).getCount();
    // when
    postReadService.readPost(targetPost.getId(), viewer.getId());
    postReadService.readPost(targetPost.getId(), viewer.getId());
    postReadService.readPost(targetPost.getId(), viewer.getId());

    // then

    assertThat(helper.postReadService.getPost(targetPost.getId()).getViewCount()).isEqualTo(old_uniqueView); // 여러번 조회하더라도, Post의 조회수는 유니크한 대상을 기준으로 증가한다.
    assertThat(postViewRepository.findByPostIdAndUserId(targetPost.getId(), viewer.getId()).getCount()).isGreaterThan(old_repeativeView); // 최초 1번 + 3번 = 4번이 카운팅 되어야한다.


  }

}