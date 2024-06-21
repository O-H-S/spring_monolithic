package com.ohs.monolithic.board;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostReadService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PostCountCacheRegister implements ApplicationRunner {

    //private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final PostReadService postService;

    @Override
    public void run(ApplicationArguments args) {
        ConcurrentHashMap<Integer, Long> postCountMap = new ConcurrentHashMap<>();

        //List<Board> boards = boardRepository.findAll();
        List<Board> boards = boardService.getBoardsRaw();

        boards.forEach(board -> {
            if(board.getDeleted() == null) // 기존 데이터 호환성 위해
            {
                board.setDeleted(false);
                boardService.save(board);
            }
            if(board.getPaginationType() == null) // 기존 데이터 호환성 위해
            {
                board.setPaginationType(BoardPaginationType.Offset_CountCache_CoveringIndex);
                boardService.save(board);
            }

            if(!board.getDeleted()) {
                Long postCount = board.getPostCount();
                /*if (postCount == null) {
                    postCount = postService.calculateCount(board.getId());
                }*/
                postCountMap.put(board.getId(), postCount);
            }

        });

        postCountMap.forEach((id, count) -> System.out.println("- Board ID: " + id + ", Init Post Count: " + count));
        boardService.registerPostCountCache(postCountMap);

    }

    @PreDestroy
    public void onExit() {
        boardService.savePostCounts();
    }

}