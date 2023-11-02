package com.ohs.monolithic.board;



import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.repository.BoardRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class BoardRepositoryTest {

    @Autowired // datajpatest 어노테이션이 관련 context를 로드하기 때문에 가능하다.
    BoardRepository boardRepository;


    @Test
    @DisplayName("게시판 생성")
    public void creatingBoard(){
        // given
        LocalDateTime creationTime = LocalDateTime.now();
        final Board board = Board.builder()
                .title("자유")
                .createDate(creationTime)
                .description("자유롭게 사용하는 공간입니다.")
                .build();

        // when
        final Board result = boardRepository.save(board);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("자유");
        assertThat(result.getCreateDate()).isEqualTo(creationTime);
        assertThat(result.getDescription()).isEqualTo("자유롭게 사용하는 공간입니다.");

    }

    @Test
    @DisplayName("게시판 이름 중복 가능")
    public void availableDuplicatedName(){
        // given
        LocalDateTime creationTime = LocalDateTime.now();
        final Board board = Board.builder()
                .title("자유")
                .createDate(creationTime)
                .description("자유롭게 사용하는 공간입니다.")
                .build();
        Board board_result = boardRepository.save(board);

        //when
        final Board board2 = Board.builder()
                .title("자유")
                .createDate(creationTime)
                .description("자유롭게 사용하세요.")
                .build();
        Board board2_result = boardRepository.save(board2);

        //then
        assertThat(board_result.getId() == board2_result.getId()).isEqualTo(false);
    }


    /*@BeforeAll // 전체 테스트를 시작하기 전에 1회 실행하므로 메서드는 static으로 선언
    static void beforeAll() {
        System.out.println("@BeforeAll");
    }
    @BeforeEach // 테스트 케이스를 시작하기 전마다 실행
    public void beforeEach() {
        System.out.println("@BeforeEach");
    }
    @Test
    public void test1() {
        System.out.println("test1");
    }
    @Test
    public void test2() {
        System.out.println("test2");
    }
    @Test
    public void test3() {
        System.out.println("test3");
    }
    @AfterAll // 전체 테스트를 마치고 종료하기 전에 1회 실행하므로 메서드는 static으로 선언
    static void afterAll() {
        System.out.println("@AfterAll");
    }
    @AfterEach // 테스트 케이스를 종료하기 전마다 실행
    public void afterEach() {
        System.out.println("@AfterEach");
    }*/
}