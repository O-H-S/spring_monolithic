package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.dto.BoardCreationForm;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.common.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BoardInternalService {
  final BoardRepository boardRepository;


  final CacheManager cacheManager; // For board dto
  final CacheManager permanentCacheManager; // For board post count

  final RedisTemplate<String, Object> redisTemplate;

  @Transactional
  @CachePut(cacheNames = "boards", key = "#result.id")
  public BoardResponse createBoardInternal(String title, String desc, BoardPaginationType paginationType) {

    Board newBoard = Board.builder()
            .title(title)
            .description(desc)
            .paginationType(paginationType)
            .build();

    Board resultBoard = boardRepository.save(newBoard);

    Cache boardPostCountsCache = permanentCacheManager.getCache("boardPostCounts");
    if (boardPostCountsCache != null) {
      boardPostCountsCache.put(resultBoard.getId(), 0L);
    }


    return BoardResponse.fromEntity(resultBoard, 0L);
  }


  @Transactional
  @CachePut(cacheNames = "boards", key = "#boardId")
  public BoardResponse updateBoardInternal(Integer boardId, BoardCreationForm form){
    assertBoardExists(boardId); // 여기서 영속성 컨텍스트에 로드됨.
    Board target = boardRepository.getReferenceById(boardId);
    target.setTitle(form.getTitle());
    target.setDescription(form.getDesc());
    target.setPaginationType(form.getPaginationType());
    boardRepository.save(target);
    return BoardResponse.fromEntity(target, null);
  }

  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "boards", key = "#id")
  public BoardResponse getBoardInternal(Integer id) {
    assertBoardExists(id); // 여기서 영속성 컨텍스트에 로드됨.
    Board board = boardRepository.getReferenceById(id);
    return BoardResponse.fromEntity(board, null, null, null);
  }

  @Transactional
  @CacheEvict(cacheNames = "boards", key = "#id")
  public void deleteBoardInternal(Integer id) {
    assertBoardExists(id); // 여기서 영속성 컨텍스트에 로드됨.

    Board target = boardRepository.getReferenceById(id);
    target.setDeleted(true);
    boardRepository.save(target);

  }

  @Transactional(readOnly = true)
  public Board getBoardFromTitleInternal(String title){
    return boardRepository.findByTitleAndDeletedFalse(title);
  }

  public Long getPostCount(Integer boardId){
    Cache boardPostCountsCache = permanentCacheManager.getCache("boardPostCounts");
    Object count = boardPostCountsCache.get(boardId).get();
    if(count instanceof Integer intCount)
      return intCount.longValue();
    return (Long)count;
  }

  public List<Long> getPostCounts(List<Integer> boardIds) {

    List<String> keys = boardIds.stream()
            .map(id -> "boardPostCounts::" + id)
            .collect(Collectors.toList());

    List<Long> values = redisTemplate.opsForValue().multiGet(keys).stream()
            .map(value -> value != null ? ((Integer)value).longValue() : 0L)
            .collect(Collectors.toList());

    return values;
  }

  public void loadPostCounts(){
    List<Board> boards = boardRepository.findAllByDeletedFalse();
    List<String> keys = boards.stream()
            .map(board -> "boardPostCounts::" + board.getId())
            .collect(Collectors.toList());

    // Redis multiGet 사용
    List<Object> cachedValues = redisTemplate.opsForValue().multiGet(keys);

    for (int i = 0; i < boards.size(); i++) {
      if (cachedValues.get(i) == null) {
        Integer boardId = boards.get(i).getId();
        Long postCount = boards.get(i).getPostCount();
        redisTemplate.opsForValue().set("boardPostCounts::" + boardId, postCount == null?0L : postCount);
      }
    }

  }

  public void savePostCounts(){
    List<Board> boards = boardRepository.findAllByDeletedFalse();
    List<Integer> boardIds = boards.stream().map(Board::getId).toList();

    List<Long> postCounts = getPostCounts(boardIds);
    for (int i = 0; i < boards.size(); i++) {
      Long count = postCounts.get(i);
      if(count != null)
        boards.get(i).setPostCount(count);
    }

    boardRepository.saveAll(boards);
  }



  @Transactional
  public void incrementPostCount(Integer boardId, long delta) {
    String key = "boardPostCounts::" + boardId;

    redisTemplate.opsForValue().increment(key, delta);
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if (status == STATUS_ROLLED_BACK) {
          redisTemplate.opsForValue().increment(key, -delta);
        }
      }
    });
  }



  public void assertBoardExists(Integer id) {
    if (id == null || id < 0) {
      throw new IllegalArgumentException("올바르지 않은 Board id 입니다.");
    }
    Optional<Board> targetOp = boardRepository.findByIdAndDeleted(id, false);
    if(targetOp.isEmpty()){
      throw new DataNotFoundException("board", "존재하지 않는 게시판입니다.");
    }
  }




}
