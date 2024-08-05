package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;

import com.ohs.monolithic.board.repository.PostRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostPaginationService {
  private final BoardInternalService boardInternalService;
  private final BoardService boardService;
  private final PostRepository postRepository;
  private final PostTagService postTagService;

  @PersistenceContext
  EntityManager em;

  // 게시판 설정에 따른 Posts 페이지 조회 동작


  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getPostListAsPage(Integer boardId, Integer page, Integer count){
    return getPostListAsPage(boardId, page, count, page);
  }

  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getPostListAsPage(Integer boardId, Integer page, Integer count, Integer oldPage){

    BoardResponse targetBoard = boardInternalService.getBoardInternal(boardId);

    switch (targetBoard.getPaginationType()){

      case Offset -> {
        return getList_Legacy(page, boardId);
      }
      case Offset_CountCache -> {
        return getListWithoutCounting_Legacy(page, boardId, count);
      }
      case Offset_CountCache_CoveringIndex -> {
        return getListWithCovering(page, boardId, count);
      }
      case Cursor -> {
        return null;
        /*throw new RuntimeException("이 게시판의 PaginationType은 Page 형식으로 조회할 수 없습니다.");*/
      }

      case Hybrid -> {
        throw new UnsupportedOperationException("준비 중");
      }

      default -> {
        return null;
      }
    }

  }

  @Transactional(readOnly = true)
  public List<PostPaginationDto> getPostListAsScroll(Integer boardId, Long lastPostId, Integer count){
    List<PostPaginationDto> result = getListWithoutOffset(lastPostId, boardId, count);
    return result;
  }


    /*
        Post 페이지네이션의 다양한 구현들
    */

  // Offset, No count cache, No covering index
  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getList_Legacy(int page, Integer boardID) {
    Board boardReference = em.getReference(Board.class, boardID);

    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    // JpaRepository의 기본 구현으로는 Count 쿼리 발생
    Page<Post> raw = this.postRepository.findAllByBoardAndDeletedFalse(pageable, boardReference);

    // Post 엔티티를 PostPaginationDto로 변환 (N+1 발생)
    Page<PostPaginationDto> transformed = raw.map(PostPaginationDto::of);

    return transformed;
  }
  // Offset, Count cache, No covering index
  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getListWithoutCounting_Legacy(int page, Integer boardID, Integer count) {
    Board boardReference = em.getReference(Board.class, boardID);
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, count, Sort.by(sorts));
    return this.postRepository.selectAllByBoard(pageable, boardReference, boardInternalService.getPostCount(boardID));
  }

  // Offset, Count cache, Covering index

  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getListWithCovering(int page, Integer boardID, Integer count, List<String> tags) {
    Board boardReference = em.getReference(Board.class, boardID);
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, count, Sort.by(sorts));

    Page<PostPaginationDto> result  = this.postRepository.selectAllByBoardWithCovering(pageable, boardReference, boardInternalService.getPostCount(boardID), postTagService.getTagFilter(tags));

    List<Long> postIds = result.stream()
            .map(PostPaginationDto::getId)
            .toList();

    Map<Long, List<PostTag>> tagTables = postTagService.getPostListTags(postIds);
    for(PostPaginationDto dto : result){
      List<PostTag> tagsPerPost = tagTables.get(dto.getId());
      if(tagsPerPost != null)
        dto.mapTags(tagsPerPost);
    }

    return result;
  }
  @Transactional(readOnly = true)
  public Page<PostPaginationDto> getListWithCovering(int page, Integer boardID, Integer count) {
    return getListWithCovering(page, boardID, count, null);
  }

  // No offset(Cursor)
  @Transactional(readOnly = true)
  public List<PostPaginationDto> getListWithoutOffset(Long baseID, Integer boardID, Integer count) {
    Board boardReference = em.getReference(Board.class, boardID);
    return this.postRepository.selectNextByBoard(baseID, boardReference, count);
  }

}
