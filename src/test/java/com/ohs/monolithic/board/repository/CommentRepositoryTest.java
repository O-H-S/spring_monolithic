package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.board.utils.BoardIntegrationTestHelper;
import com.ohs.monolithic.board.utils.BoardRepositoryTestHelper;
import com.ohs.monolithic.configuration.QuerydslConfig;
import com.ohs.monolithic.user.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest // 각 테스트 실행 후에 롤백, @Transactional 적용되어 있음.
@Tag("base")
@Tag("integrate-limited")
@Import({QuerydslConfig.class, BoardRepositoryTestHelper.class})
// @DataJpaTest에서는 직접 BoardRepositoryTestHelper 를 지정해줘야 빈이 로드된다.
// BoardIntegrationTestHelper는 Service 빈들에 의존하고 있는데, DataJpaTest에서는 Service 빈들을 위한 의존성들을 해결하지 않는다.
public class CommentRepositoryTest {

  @Autowired
  CommentRepository commentRepository;


  @Autowired
  BoardRepositoryTestHelper helper;

  @AfterEach
  void teardown(){

    // @DataJpaTest는 각 테스트 후 롤백되므로 db 초기화는 필요 없지만 헬퍼 객체가 내부적으로 사용하는 변수들이 존재함.
    helper.resetCounts();
  }

  @Test
  @DisplayName("getCommentsByPost(Post, Account) : 댓글이 없으면 비어있는 리스트를 반환한다.")
  void getCommentsByPost_Empty(){
    // given
    Board board = helper.establishBoard();
    Account member = helper.establishMember();
    Post post = helper.writePostTo(board,member);

    //when
    List<CommentPaginationDto> found = commentRepository.getCommentsByPost(post, member);

    //then
    assertThat(found).isEmpty();

  }


  @Test
  @DisplayName("getCommentsByPost(Post, Account) : 유저가 댓글 목록을 조회할 때, 추천한 댓글이면 liked가 true이고 아니면 false 이다.")
  public void getCommentsByPost(){

    // given
    Board board = helper.establishBoard();
    Account member = helper.establishMember();
    Post post = helper.writePostTo(board,member);

    List<Account> members = helper.establishMembers(10);
    List<Comment> comments = helper.writeCommentsTo(post, members, 30);

    int likedCount = 10;
    Account viewer = members.get(0);
    HashSet<Long> likedSet = new HashSet<>(); // 검증용 변수
    for (int i = 0; i < likedCount; i++) {
      helper.likeTo(comments.get(i), viewer);
      likedSet.add(comments.get(i).getId());
    }


    //when
    List<CommentPaginationDto> found = commentRepository.getCommentsByPost(post, viewer);


    // then
    long likedCommentsCount = found.stream()
            .filter(CommentPaginationDto::getLiked)
            .map(commentPaginationDto -> likedSet.remove(commentPaginationDto.getId()))
            .count();

    assertThat(likedCommentsCount).isEqualTo(likedCount);
    assertThat(likedSet).isEmpty();

  }

  @Test
  @DisplayName("getCommentsByPost(Post, null) : 로그인 하지 않은 상태이면, null로 입력한다. ")
  void getCommentsByPost_NotLogged(){
    // given
    Board board = helper.establishBoard();
    Account member = helper.establishMember();
    Post post = helper.writePostTo(board,member);

    List<Account> members = helper.establishMembers(10);
    int commentCounts = 15;
    List<Comment> comments = helper.writeCommentsTo(post, members, commentCounts);

    //when
    List<CommentPaginationDto> found = commentRepository.getCommentsByPost(post, null);

    //then
    assertThat(found).hasSize(commentCounts);
  }


}
