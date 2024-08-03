package com.ohs.monolithic.board;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.service.BoardInternalService;
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
    private final BoardInternalService boardInternalService;

    @Override
    public void run(ApplicationArguments args) {
        boardInternalService.loadPostCounts();
    }

    @PreDestroy
    public void onExit() {
        boardInternalService.savePostCounts();
    }

}