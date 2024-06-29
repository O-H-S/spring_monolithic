package com.ohs.monolithic.board.repository;



import com.ohs.monolithic.common.configuration.QuerydslConfig;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import com.ohs.monolithic.utils.RepositoryTestBase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;



class BoardRepositoryTest extends RepositoryTestBase {

    @Autowired // datajpatest 어노테이션이 관련 context를 로드하기 때문에 가능하다.
    BoardRepository boardRepository;

    @Test
    @DisplayName("save(Board) : 최초 생성")
    public void creatingBoard(){
        // given

        // when
        final Board board = BoardTestUtils.createBoardSimple(null, "자유", "자유 게시판 설명");

        final Board result = boardRepository.save(board);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("자유");
        assertThat(result.getCreateDate()).isEqualTo(board.getCreateDate());
        assertThat(result.getDescription()).isEqualTo("자유 게시판 설명");

    }

    @Test
    @DisplayName("save(Board) : 중복된 title 가능.")
    public void availableDuplicatedName(){
        // given
        // 기존에 "자유" 게시판이 존재한다.
        final Board board = BoardTestUtils.createBoardSimple(null, "자유", "자유 게시판 설명");
        Board board_result = boardRepository.save(board);

        //when
        final Board board2 = BoardTestUtils.createBoardSimple(null, "자유", "새로운 자유 게시판");
        Board board2_result = boardRepository.save(board2);

        //then
        // 새로운 "자유" 게시판을 생성해도 별개의 게시판으로 인식된다.
        assertThat(board_result.getId() != board2_result.getId());

    }

    @Test
    @DisplayName("getAllBoards(boolean, boolean)) : 파라미터로 조회할 목록 설정 가능.")
    public void getAllBoards_partial(){
        // given
        // 기존에 게시판들이 존재한다.
        Board board_result = boardRepository.save(
                BoardTestUtils.createBoardSimple(null, "자유", "자유 게시판 설명")
        );
        Board board_result2 = boardRepository.save(
                BoardTestUtils.createBoardSimple(null, "건의", "건의 게시판 설명")
        );

        //boardRepository.
        //when

        //List<Board> test = boardRepository.findAll();

        List<BoardResponse> result = boardRepository.getAllBoards(true, false);
        List<BoardResponse> result2 = boardRepository.getAllBoards(false, true);
        List<BoardResponse> result3 = boardRepository.getAllBoards(false, false);
        List<BoardResponse> result4 = boardRepository.getAllBoards(true, true);
        //then
        // 새로운 "자유" 게시판을 생성해도 별개의 게시판으로 인식된다.
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result2 != null);
        assertThat(result3 != null);
        assertThat(result4 != null);
        //assertThat(board_result.getId() == board2_result.getId()).isEqualTo(false);
    }

    @Test
    @DisplayName("getAllBoards(true, true) : 게시판이 존재하지 않으면 빈 리스트 반환.")
    public void getAllBoards_empty(){
        // given

        //when

        List<BoardResponse> result = boardRepository.getAllBoards(true, true);

        //then

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(0);
        //assertThat(board_result.getId() == board2_result.getId()).isEqualTo(false);
    }


    @Test
    @DisplayName("deleteBoard(Integer)) : 게시판 soft delete")
    public void deleteBoard_partial(){
        // given
        // 기존에 게시판들이 존재한다.
        Board board_result = boardRepository.save(
                BoardTestUtils.createBoardSimple(null, "자유", "자유 게시판 설명")
        );
        Board board_result2 = boardRepository.save(
                BoardTestUtils.createBoardSimple(null, "건의", "건의 게시판 설명")
        );

        //when

        boardRepository.deleteBoard(board_result.getId());

        //then
        List<BoardResponse> boards = boardRepository.getAllBoards(false, false);
        assertThat(boards).hasSize(1);
        //boardRepository.findAll()

    }

}