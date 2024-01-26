package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostView;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.user.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository repository;
    private final PostViewService postViewService;
    private final PostLikeService postLikeService;
    private final BoardService bService;

    @PersistenceContext
    EntityManager em;

    @Transactional(readOnly = true)
    public PostDetailResponse readPost(Long postID, Account viewer){
        Post targetPost = getPost(postID, true);
        Boolean isLiked = Boolean.FALSE;
        Boolean isMine = Boolean.FALSE;
        if(viewer != null) {
            // 조회수 카운팅 트랜잭션이 실패하더라도 게시글을 반환한다.
            try {
                postViewService.view(targetPost, viewer); // 독립된 트랜잭션
            } catch (Exception e) {
                System.out.println("Error updating post view count. : " + e.toString());
            }

            isLiked = postLikeService.doesLikePost(postID, viewer.getId());
            isMine = viewer.getId().equals( targetPost.getAuthor().getId());
        }

        return PostDetailResponse.of(targetPost, isMine, isLiked);
    }

    public Post getPost(Long id){
        return this.getPost(id, false);
    }

    // Java 메서드에서 기본값을 직접 지정하는 것은 불가능합니다.

    public Post getPost(Long id, Boolean relatedData ) {

        Optional<Post> question = null;
        if (relatedData)
            question = this.repository.findWithAuthorAndBoard(id);
        else{
            question = this.repository.findById(id);
        }
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    // test method
    @Transactional
    public Page<Post> getListLegacy(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.findAllByBoard(pageable, boardReference);
    }
    // test method
    @Transactional
    public Page<Post> getListWithoutCounting(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.selectAllByBoard(pageable, boardReference, bService.getPostCount(boardID));
    }

    @Transactional
    public Page<PostPaginationDto> getListWithCovering(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.selectAllByBoardWithCovering(pageable, boardReference, bService.getPostCount(boardID));
    }

    @Transactional
    public List<PostPaginationDto> getListWithoutOffset(Long baseID, Integer boardID, Integer count) {
        Board boardReference = em.getReference(Board.class, boardID);
        return this.repository.selectNextByBoard(baseID, boardReference, count);
    }


    /*@Transactional
    public Page<Post> getList(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.findAllByBoard(pageable, boardReference);
    }*/


   @Transactional(readOnly = true)
    public Long calculateCount(Integer boardId) {
        return repository.countByBoardId(boardId);
    }

}





