package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    public List<Comment> findAllByPost(Post post);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countCommentsByPostId(@Param("postId") Integer postId);


}