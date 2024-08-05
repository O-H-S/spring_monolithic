package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;



public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {



   /*
    FETCH: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 LAZY로 패치
    LOAD: entity graph에 명시한 attribute는 EAGER로 패치하고, 나머지 attribute는 entity에 명시한 fetch type이나 디폴트 FetchType으로 패치 (e.g. @OneToMany는 LAZY, @ManyToOne은 EAGER 등이 디폴트이다.)
    */
    @EntityGraph(attributePaths = {"board", "author"})
    @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
    Optional<Post> findWithAssociations(@Param("id") Long id);

   /*@EntityGraph(attributePaths = {"board", "author", "postTags", "postTags.tag"})
   @Query("SELECT p FROM Post p WHERE p.id = :id and p.deleted = false")
   Optional<Post> findWithAssociations(@Param("id") Long id);*/


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
    Page<Post> findAllByBoardAndDeletedFalse(Pageable pageable, Board board);



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