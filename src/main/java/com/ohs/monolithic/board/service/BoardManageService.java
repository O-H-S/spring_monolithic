package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.exception.BoardException;
import com.ohs.monolithic.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
@Service
public class BoardManageService {
    final BoardRepository bRepo;
    private ConcurrentHashMap<Integer, Long> postCountCache;
    public void registerPostCountCache(ConcurrentHashMap<Integer, Long> cache){
        postCountCache = cache;
    }

    public void incrementPostCount(Integer boardId) {
        postCountCache.compute(boardId, (key, count) -> count == null ? 1 : count + 1);
    }

    public void incrementPostCount(Integer boardId, int delta) {
        postCountCache.compute(boardId, (key, count) -> count == null ? delta : count + delta);
    }

    public void decrementPostCount(Integer boardId) {
        postCountCache.computeIfPresent(boardId, (key, count) -> count > 1 ? count - 1 : null);
    }

    public void savePostCounts(){
        List<Board> boards = getBoardsRaw();
        boards.forEach(board -> { board.setPostCount(postCountCache.getOrDefault(board.getId(),0L)); });
        bRepo.saveAll(boards);
    }

    @Transactional
    public BoardResponse createBoard(String title, String desc){


        Board newBoard = Board.builder()
                        .title(title)
                        .createDate(LocalDateTime.now())
                        .description(desc)
                        .postCount(0L)
                        .build();

        Board resultBoard =  bRepo.save(newBoard);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void beforeCommit(boolean readOnly) {
                postCountCache.putIfAbsent(resultBoard.getId(), 0L);
            }
        });


        return BoardResponse.builder()
                .id(resultBoard.getId()).title(resultBoard.getTitle()).description(resultBoard.getDescription()).build();
    }

    public Long getPostCount(Integer id) {
        Long count = postCountCache.get(id);
        return count;
    }
    public List<Board> getBoardsRaw() {
        return bRepo.findAll();
    }
    public List<BoardResponse> getBoards(){

        List<BoardResponse> results = new ArrayList<>();
        bRepo.findAll().forEach(board -> {
            results.add(
                    BoardResponse.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .description(board.getDescription())
                            .postCounts(this.getPostCount(board.getId()))
                            .build());}

        );
        return results;
    }
    public Board getBoard(Integer id){
        if(id == null || id < 0){
            throw new BoardException("올바르지 않은 ID 형식 입니다.");
        }
        Long count = postCountCache.get(id);
        if(count == null)
            throw new BoardException("존재하지 않은 게시판입니다.");
        return bRepo.findById(id).get();
    }
    public Boolean isExist(Integer id){
        if (id == null || id < 0) {
            return false;
        }
        return postCountCache.containsKey(id);
    }

    public void save(Board target) {
        bRepo.save(target);
    }


}
