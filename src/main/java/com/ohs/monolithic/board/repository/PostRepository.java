package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;



public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {



    @EntityGraph(attributePaths = {"board", "author"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
    Optional<Post> findWithAuthorAndBoard(@Param("id") Long id);


    @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
    Optional<Post> findById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
    Optional<Post> findByIdWithReadLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
    Optional<Post> findByIdWithWriteLock(@Param("id") Long id);

    //Post findById(Integer id, LockModeType lockModeType);


    // legacy
    Page<Post> findAllByBoard(Pageable pageable, Board board);



    @Modifying
    //@Transactional
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + (:delta) WHERE p.id = :postId")
    //@Lock(LockModeType.PESSIMISTIC_READ)
    void updateCommentCount(Long postId, Integer delta);

    @Modifying
    //@Transactional
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + (:delta) WHERE p.id = :postId")
        //@Lock(LockModeType.PESSIMISTIC_READ)
    void updateViewCount(Long postId, Integer delta);


    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + (:delta) WHERE p.id = :postId")
    void addLikeCount(Long postId, Long delta);


    @Modifying
    @Query("UPDATE Post p SET p.commentCount = (SELECT COUNT(c) FROM Comment c WHERE c.post = p) WHERE p.id = :postId")
    void refreshCommentCount(@Param("postId") Long postId);

    Long countByBoardId(Integer boardId);
}