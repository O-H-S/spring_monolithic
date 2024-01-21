package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.exception.BoardNotFoundException;
import com.ohs.monolithic.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
@Service
public class BoardService {
  final BoardRepository bRepo;
  private ConcurrentHashMap<Integer, Long> postCountCache;

  public void registerPostCountCache(ConcurrentHashMap<Integer, Long> cache) {
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

  public void savePostCounts() {

    List<Board> boards = getBoardsRaw();
    boards.forEach(board -> {
      if (postCountCache.containsKey(board.getId()))
        board.setPostCount(postCountCache.getOrDefault(board.getId(), 0L));
    });
    bRepo.saveAll(boards);
  }


  @Transactional
  public BoardResponse createBoard(String title, String desc) {


    Board newBoard = Board.builder()
            .title(title)
            .description(desc)
            .build();


    Board resultBoard = bRepo.save(newBoard);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK)
          postCountCache.remove(resultBoard.getId());
      }
    });
    postCountCache.putIfAbsent(resultBoard.getId(), 0L);


    return BoardResponse.builder()
            .id(resultBoard.getId()).title(resultBoard.getTitle()).description(resultBoard.getDescription()).build();
  }


  // Test Exist
  @Transactional
  public void deleteBoard(Integer id) {
    Long oldCount = -1L;
    try {
      Board target = getBoard(id);
      //Long oldCount = postCountCache.getOrDefault(id,0L);
      oldCount = postCountCache.remove(id);
            /*TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if(status == STATUS_ROLLED_BACK) {
                        postCountCache.put(id, oldCount);
                    }
                }
            });*/
      target.setPostCount(oldCount);
      target.setDeleted(true);
      bRepo.save(target);
    } catch (Exception e) {
      if (oldCount > -1)
        postCountCache.put(id, oldCount);
      throw e;
    }
    //bRepo.deleteBoard(id);

  }

  public Long getPostCount(Integer id) {
    Long count = postCountCache.get(id);
    return count;
  }

  public List<Board> getBoardsRaw() {
    return bRepo.findAll();
  }

  @Transactional(readOnly = true)
  public List<BoardResponse> getBoardsReadOnly(boolean includeTitle, boolean includeDesc) {
    List<BoardResponse> result = bRepo.getAllBoards(includeTitle, includeDesc);
    result.forEach(x -> x.setPostCounts(this.getPostCount(x.getId())));
    return result;
  }

  @Transactional(readOnly = true)
  public List<BoardResponse> getBoardsReadOnly() {
    return getBoardsReadOnly(true, true);
  }


  @Transactional
  public List<BoardResponse> getBoards() {

    List<BoardResponse> results = new ArrayList<>();
    bRepo.findAll().forEach(board -> {
              results.add(
                      BoardResponse.builder()
                              .id(board.getId())
                              .title(board.getTitle())
                              .description(board.getDescription())
                              .postCounts(
                                      this.getPostCount(board.getId())
                              )
                              .build());
            }

    );
    return results;
  }

  public Board getBoard(Integer id) throws BoardNotFoundException {
    if (id == null || id < 0) {
      throw new BoardNotFoundException(id, "올바르지 않은 ID 형식 입니다.");
    }
    Long count = postCountCache.get(id);
    if (count == null)
      throw new BoardNotFoundException(id, "존재하지 않는 게시판입니다.");
    try {
      return bRepo.findById(id).get();
    } catch (Exception e) {
      throw new BoardNotFoundException(id, "internal error");
    }
  }

  @Transactional(readOnly = true)
  public BoardResponse getBoardReadOnly(Integer id) throws BoardNotFoundException {
    assertBoardExists(id);

    Board target = bRepo.findById(id).get();
    return BoardResponse.fromEntity(target, postCountCache.get(id));
  }


  public Boolean isExist(Integer id) throws IllegalArgumentException {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("올바르지 않은 Board id 입니다.");
    }
    return postCountCache.containsKey(id);
  }

  public void assertBoardExists(Integer id) {
    try {
      if (!isExist(id)) {
        throw new BoardNotFoundException(id, "존재하지 않는 게시판입니다.");
      }
    } catch (Exception e) {
      throw new BoardNotFoundException(id, "올바르지 않은 포맷입니다.");
    }
  }

  public void save(Board target) {
    bRepo.save(target);
  }


}
