package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {
    public List<Comment> findAllByPost(Post post);

    @Query("select c from Comment c join fetch c.author where c.deleted = false and c.post.id = :#{#targetPost.id}")
    public List<Comment> findAllByPostWithUser(@Param("targetPost") Post post);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countCommentsByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + (:delta) WHERE c.id = :commentID")
    void addLikeCount(Long commentID, Long delta);

    @Query("SELECT c.likeCount FROM Comment c WHERE c.id = :commentId")
    Long findLikeCountById(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c WHERE c.deleted = false AND c.id = :id")
    Comment getComment(@Param("id") Long id);

}