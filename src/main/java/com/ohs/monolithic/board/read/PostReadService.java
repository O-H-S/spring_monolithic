package com.ohs.monolithic.board.read;


import com.ohs.monolithic.board.Board;
import com.ohs.monolithic.board.Post;
import com.ohs.monolithic.board.PostRepository;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository repository;
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
            question = this.repository.findAsCompleteById(id);
        else{
            question = this.repository.findById(id);
        }
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public List<Post> getRecentPosts(Board targetBoard, Integer count){
        Pageable topN = PageRequest.of(0, count);
        return repository.findByBoardOrderByCreateDateDesc(targetBoard, topN);
    }

    @Transactional
    public Page<Post> getList(int page, Integer boardID) {
        Board boardReference = em.getReference(Board.class, boardID);
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.repository.findAllByBoard(pageable, boardReference);
    }

    /*public void test(){
        List<Post> all = this.repository.findAll();
        //assertEquals(2, all.size());

        Post q = all.get(0);
        //assertEquals("sbb가 무엇인가요?", q.getTitle())
        Optional<Post> oq = this.repository.findById(1);
        if(oq.isPresent()) {
            Post resultPost = oq.get();
        }

        Optional<Post> targetPostOP = this.repository.findById(1);
        Post targetValue = targetPostOP.get();
        targetValue.setTitle("수정된 제목");
        this.repository.save(targetValue);

    }*/


}





