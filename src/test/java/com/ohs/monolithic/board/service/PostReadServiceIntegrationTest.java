package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.constants.PostTagType;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.repository.PostViewRepository;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.IntegrationTestWithMySQL;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PostReadServiceIntegrationTest extends IntegrationTestWithMySQL {

  @Autowired
  private PostReadService postReadService;

  @Autowired
  private PostViewRepository postViewRepository;

  @Test
  @DisplayName("readPost(Integer, Account): 게시글을 조회하면, viewCount가 증가하고, PostView 가 추가된다.")
  public void readPost(){
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    PostDetailResponse targetPost = givens.c;
    helper.postTagService.addPostTagList(targetPost.getId(),
            List.of("tag1", "tag2", "tag3"), List.of(PostTagType.Normal, PostTagType.Normal, PostTagType.Normal)
    , true);
    Account viewer = givens.b;

    long oldCount = givens.c.getViewCount();

    // when
    System.out.println("=====================<read start>");
    PostDetailResponse response = postReadService.readPost(targetPost.getId(), viewer.getId());
    System.out.println("======================<read end>");
    // then

    Post postInDB = helper.postReadService.getPost(targetPost.getId());


    assertThat(targetPost.getViewCount()).isEqualTo(oldCount); // 새로운 트랜잭션에서 viewCount를 증가시키므로, 기존 Post 엔티티의 viewCount는 그대로이다.
    assertThat(postInDB.getViewCount()).isEqualTo(oldCount + 1); // DB에는 증가가 적용되어 있으므로, 다시 불러오면 증가된 값이 나온다.
    assertThat(postViewRepository.findByPostIdAndUserId(targetPost.getId(), viewer.getId())).isNotNull(); // PostView가 추가되어 있어야한다.

    assertThat(targetPost.getId()).isEqualTo(response.getId());
    assertThat(targetPost.getBoardId()).isEqualTo(response.getBoardId());
    assertThat(targetPost.getUserId()).isEqualTo(response.getUserId());
    //assertThat(response.viewCount).isEqualTo(postInDB.getViewCount());
  }

  @Test
  @DisplayName("readPost(Integer, Account): 같은 사람이 여러번 조회하면, viewCount는 변화 없고 PostView의 count만 증가한다.")
  public void readPost_0(){
    Triple<BoardResponse, Account, PostDetailResponse> givens = helper.InitDummy_BoardAccountPost();
    PostDetailResponse targetPost = givens.c;
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