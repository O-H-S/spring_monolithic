package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
/*
    Extension 인터페이스와 하위 인터페이스
        BeforeAllCallback: 모든 테스트 메서드 전에 한 번 호출됩니다.
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

    ConcurrentHashMap<Integer, Long> mockPostCountCache;
    @BeforeEach
    public void setUp() {
        mockPostCountCache = new ConcurrentHashMap<>();
        target.registerPostCountCache(mockPostCountCache);

        TransactionSynchronizationManager.initSynchronization();

        // Other setup
    }

    @AfterEach
    public void tearDown() {
        TransactionSynchronizationManager.clear();
    }

    @Test
    @DisplayName("createBoard(Title, Description) : 새로운 게시판 생성")
    public void createNewBoard(){

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
        assertEquals(mockPostCountCache.get(1), 0L);
        verify(mockRepository).save(any(Board.class));
    }



}
