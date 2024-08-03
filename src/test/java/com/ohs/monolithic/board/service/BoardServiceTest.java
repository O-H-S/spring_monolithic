package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import com.ohs.monolithic.common.exception.DataNotFoundException;
import com.ohs.monolithic.utils.WithMockCustomUser;
import com.ohs.monolithic.utils.WithMockCustomUserContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
/*
    Extension 인터페이스와 하위 인터페이스
        BeforeAllCallback: 모든 테스트 메서드 전에ㄴ 한 번 호출됩니다.
        BeforeEachCallback: 각 테스트 메서드 전에 호출됩니다.
        AfterEachCallback: 각 테스트 메서드 후에 호출됩니다.
        AfterAllCallback: 모든 테스트 메서드 후에 한 번 호출됩니다.
        ParameterResolver:
*/
@Tag("base")
@Tag("unit")
public class BoardServiceTest {


  @InjectMocks
  private BoardService target;


  @Mock
  private BoardRepository mockRepository;


  @BeforeEach
  public void setUp() {

    TransactionSynchronizationManager.initSynchronization();

    // Other setup
  }

  @AfterEach
  public void tearDown() {
    TransactionSynchronizationManager.clear();
  }

  @Test
  @DisplayName("createBoard(Title, Description) : 새로운 게시판 생성")
  public void createNewBoard() {

    // Given
    String title = "Test Title";
    String desc = "Test Desc";
    Board mockBoard = BoardTestUtils.createBoardSimple(1, title, desc);
    when(mockRepository.save(any(Board.class))).thenReturn(mockBoard);

    // When
    BoardResponse response = target.createBoard(title, desc);

    // Then
    assertNotNull(response);
    assertEquals(1, response.getId());
    assertEquals(title, response.getTitle());
    assertEquals(desc, response.getDescription());
    //assertEquals(mockPostCountCache.get(1), 0L);
    verify(mockRepository).save(any(Board.class));
  }

  @Test
  @DisplayName("deleteBoard(ID) : 존재하지 않는 게시판이면 예외 발생")
  @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
  public void deleteBoard() {
    // given
    //Board mockBoard = BoardTestUtils.createBoardSimple(1, "자유", "desc");
    Integer targetID = 1;

    // when, then

    Exception exception = assertThrows(DataNotFoundException.class, () -> {
      target.deleteBoard(targetID, WithMockCustomUserContext.getAppUser());
    });

    assertThat(exception).isNotNull();
  }


  @Test
  @DisplayName("deleteBoard(ID) : 삭제 성공시, deleted 컬럼이 true로 변경되고 캐시가 제거됨.")
  @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
  public void deleteBoard_0() {
    // given
    Integer targetID = 1;
    Board mockBoard = BoardTestUtils.createBoardSimple(1, "자유", "desc");

    when(mockRepository.findById(targetID)).thenReturn(Optional.of(mockBoard));


    Long oldCount = 5L;
    //mockPostCountCache.put(targetID, oldCount);

    // when
    target.deleteBoard(targetID, WithMockCustomUserContext.getAppUser());

    // then
    assertEquals(mockBoard.getDeleted(), true);
    assertEquals(mockBoard.getPostCount(), 5L);
    //assertFalse(mockPostCountCache.containsKey(targetID));
  }

  // unit test에서는 TransactionSynchronizationManager가 실제처럼 동작하지 않기 때문에 테스트 불가능

  @Test
  @DisplayName("deleteBoard(ID) : 트랜잭션 도중 예외 발생시, 캐시 정상적으로 되돌림.")
  @WithMockCustomUser(username = "hyeonsu", authorities = "ADMIN")
  public void deleteBoard_1() {
    // given
    Integer targetID = 1;
    Board mockBoard = BoardTestUtils.createBoardSimple(1, "자유", "desc");

    when(mockRepository.findById(targetID)).thenReturn(Optional.of(mockBoard));


    Long oldCount = 5L;
    //mockPostCountCache.put(targetID, oldCount);

    doThrow(new RuntimeException()).when(mockRepository).save(mockBoard);

    // when
    assertThrows(RuntimeException.class, ()-> target.deleteBoard(targetID, WithMockCustomUserContext.getAppUser()));

    // then

    //assertTrue(mockPostCountCache.containsKey(targetID));
  }


}
