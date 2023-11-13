package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.exception.DataNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository repository;
    private final BoardManageService bService;

    @PersistenceContext
    EntityManager em;


    public List<Post> getAll(Integer boardID){
        Board boardReference = em.getReference(Board.class, boardID);
        return repository.findAllAsCompleteByBoard(boardReference);
    }

    public Post getPost(Integer id){
        return this.getPost(id, false);
    }

    // Java 메서드에서 기본값을 직접 지정하는 것은 불가능합니다.
    public Post getPost(Integer id, Boolean relatedData ) {


        Optional<Post> question = null;
        if (relatedData)
            question = this.repository.findWithCommentListById(id);
        else{
            question = this.repository.findById(id);
        }
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    @Transactional
    public Page<Post> getListLegacy(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.findAllByBoard(pageable, boardReference);
    }

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
    public List<PostPaginationDto> getListWithoutOffset(Integer baseID, Integer boardID, Integer count) {
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

   @Transactional(isolation = Isolation.READ_COMMITTED)
    public void test(){
        repository.findByTitle("testTitle");

   }



   @Transactional(readOnly = true)
    public Long calculateCount(Integer id) {
        return repository.countByBoardId(id);
    }


}





