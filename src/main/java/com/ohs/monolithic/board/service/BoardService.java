package com.ohs.monolithic.board.service;


import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.common.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
@Service
public class BoardService {
  final BoardRepository boardRepository;
  final BoardPermissionService boardPermissionService;
  private ConcurrentHashMap<Integer, Long> postCountCache;

  public void registerPostCountCache(ConcurrentHashMap<Integer, Long> cache) {
    postCountCache = cache;
  }

  // 트랜잭션 안에서 실행되어야함.
  public void incrementPostCount(Integer boardId) {
    postCountCache.compute(boardId, (key, count) -> count == null ? 1 : count + 1);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if(status == STATUS_ROLLED_BACK)
          postCountCache.compute(boardId, (key, count) -> count != null ? count - 1L : null);
      }
    });

  }

  // 트랜잭션 안에서 실행되어야함.
  public void incrementPostCount(Integer boardId, int delta) {

    postCountCache.compute(boardId, (key, count) -> count == null ? delta : count + delta);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if(status == STATUS_ROLLED_BACK) {
          System.out.println("rollback");
          postCountCache.compute(boardId, (key, count) -> count != null ? count - delta : 0L);
        }
      }
    });

  }

  // 트랜잭션 안에서 실행되어야함.
  public void decrementPostCount(Integer boardId) {
    postCountCache.computeIfPresent(boardId, (key, count) -> count > 1 ? count - 1 : null);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if(status == STATUS_ROLLED_BACK)
          postCountCache.computeIfPresent(boardId, (key, count) -> count + 1L);
      }
    });


  }



  public void savePostCounts() {

    List<Board> boards = getBoardsRaw();
    boards.forEach(board -> {
      if (postCountCache.containsKey(board.getId()))
        board.setPostCount(postCountCache.getOrDefault(board.getId(), 0L));
    });
    boardRepository.saveAll(boards);
  }

  @Transactional
  public BoardResponse createBoard(String title, String desc){
    return createBoard(title, desc, BoardPaginationType.Offset_CountCache_CoveringIndex);
  }

  @Transactional
  public BoardResponse createBoard(String title, String desc, BoardPaginationType paginationType) {


    Board newBoard = Board.builder()
            .title(title)
            .description(desc)
            .paginationType(paginationType)
            .build();


    Board resultBoard = boardRepository.save(newBoard);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK)
          postCountCache.remove(resultBoard.getId());
      }
    });
    postCountCache.putIfAbsent(resultBoard.getId(), 0L);


    return BoardResponse.fromEntity(resultBoard, getPostCount(resultBoard.getId()));
  }

  @Transactional
  public BoardResponse updateBoard(Integer boardId, BoardCreationForm form){
    Board target = getBoard(boardId);
    target.setTitle(form.getTitle());
    target.setDescription(form.getDesc());
    target.setPaginationType(form.getPaginationType());
    boardRepository.save(target);
    return BoardResponse.fromEntity(target, getPostCount(boardId));
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
      boardRepository.save(target);
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
    return boardRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<BoardResponse> getBoardsReadOnly(boolean includeTitle, boolean includeDesc) {
    List<BoardResponse> result = boardRepository.getAllBoards(includeTitle, includeDesc);
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
    boardRepository.findAll().forEach(board -> {
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

  public Board getBoard(Integer id) throws DataNotFoundException {
    if (id == null || id < 0) {
      throw new DataNotFoundException("board", "올바르지 않은 ID 형식 입니다.");
    }
    Long count = postCountCache.get(id);
    if (count == null)
      throw new DataNotFoundException("board", "존재하지 않는 게시판입니다.");
    try {
      return boardRepository.findById(id).get();
    } catch (Exception e) {
      throw new DataNotFoundException("board", "internal error");
    }
  }

  @Transactional(readOnly = true)
  public BoardResponse getBoardReadOnly(Integer id, AppUser user) throws DataNotFoundException {
    assertBoardExists(id);

    Board target = boardRepository.findById(id).get();
    if(user == null)
      return BoardResponse.fromEntity(target, postCountCache.get(id));
    var response = boardPermissionService.getPermissionResponse(id, user.isAdmin()? UserRole.ADMIN:UserRole.USER);

    return BoardResponse.fromEntity(target, postCountCache.get(id), response.isRoleWritable, response.writableMethods);

  }


  public Boolean isExist(Integer id) throws IllegalArgumentException {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("올바르지 않은 Board id 입니다.");
    }
    return postCountCache.containsKey(id);
  }

  @Transactional(readOnly = true)
  public Board getBoardFromTitle(String title){
    return boardRepository.findByTitle(title);
  }

  public void assertBoardExists(Integer id) {
    try {
      if (!isExist(id)) {
        throw new DataNotFoundException("board", "존재하지 않는 게시판입니다.");
      }
    } catch (Exception e) {
      throw new DataNotFoundException("board", "올바르지 않은 포맷입니다.");
    }
  }

  public void save(Board target) {
    boardRepository.save(target);
  }


}
