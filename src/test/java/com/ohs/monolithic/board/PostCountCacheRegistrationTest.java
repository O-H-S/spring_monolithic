package com.ohs.monolithic.board;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.service.BoardManageService;
import com.ohs.monolithic.board.service.PostReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class PostCountCacheRegistrationTest {

    @Mock
    private BoardManageService boardService;
    @Mock
    private PostReadService postReadService;

    @InjectMocks // 실제 인스턴스에 목 객체를 주입
    private PostCountCacheRegister register;

    @Test
    @DisplayName("컨텍스트 로드 후, Board의 마지막 저장된 postCount를 캐싱")
    public void cacheInitialRegistrationTest() throws Exception {
        Function<Integer, Long> desirableCount = boardId -> 10L + boardId;
        // 실제 게시글 수는 각각 11, 12,13, 14, 15개라고 가정한다.

        //given
        // 저장된 게시글 수 컬럼 값은 각각 null, null, 13, 14, 15개
        int boardCount = 5;
        int invalidatedCount = 2;
        List<Board> boards = BoardTestUtils.createBoardList(boardCount);
        for (int i = 0; i < boardCount; i++) {
            if(i < invalidatedCount) {
                boards.get(i).setPostCount(null);
            }
            else
                boards.get(i).setPostCount(desirableCount.apply(boards.get(i).getId()));
        }

        when(boardService.getBoardsRaw()).thenReturn(boards);
        when(postReadService.calculateCount(anyInt())).thenAnswer(invocation -> desirableCount.apply( (Integer) invocation.getArguments()[0]));
        ApplicationArguments mockArgs = mock(ApplicationArguments.class);

        // when
        register.run(mockArgs);


        // Then
        ConcurrentHashMap<Integer, Long> expectedPostCountMap = new ConcurrentHashMap<>();
        boards.forEach(board -> {
                    Long postCount = board.getPostCount() != null ? board.getPostCount() : desirableCount.apply( board.getId());
                    expectedPostCountMap.put(board.getId(), postCount);
                }
        );

        verify(boardService).getBoardsRaw();
        verify(postReadService, times(invalidatedCount).description("postCount가 null인 게시판의 개수만큼, 다시 계산한다."))
                .calculateCount(anyInt());
        verify(boardService).registerPostCountCache(expectedPostCountMap);
    }
}