package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    //@EntityGraph(attributePaths = { "author.address"}) 이런식으로 중첩된 동작 가능.
    //  Integer id가 실제로 어떤 엔터티를 찾을지 결정하는 기준이 됩니
    //  다??

    @EntityGraph(attributePaths = {"author", "commentList"}, type = EntityGraph.EntityGraphType.LOAD)
    // Eager Loading이 적용
    Optional<Post> findWithCommentListById(Integer id);

    //Post findById(Integer id, LockModeType lockModeType);

    Post findByTitle(String title);
    Post findByTitleLike(String title); // 입력 예시 :  %sbb%
    Post findByTitleAndContent(String title, String content);

    List<Post> findAllByBoard(Board board);
    @EntityGraph(attributePaths = {"commentList"})
    List<Post> findAllAsCompleteByBoard(Board board);

    List<Post> findByBoardOrderByCreateDateDesc(Board board, Pageable pageable);

    // @EntityGraph(attributePaths = {"commentList"}, type = EntityGraph.EntityGraphType.LOAD)
    // 일대다 관계에서는 불가능한듯? 아니면, list<post> 형태에서 불가능한듯?
    Page<Post> findAll(Pageable pageable);
    Page<Post> findAllByBoard(Pageable pageable, Board board);


    @Modifying
    //@Transactional
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + (:delta) WHERE p.id = :postId")
    //@Lock(LockModeType.PESSIMISTIC_READ)
    void updateCommentCount(Integer postId, Integer delta);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = (SELECT COUNT(c) FROM Comment c WHERE c.post = p) WHERE p.id = :postId")
    void refreshCommentCount(@Param("postId") Integer postId);


}