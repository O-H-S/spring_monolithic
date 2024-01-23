package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.user.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CommentService {
    final CommentRepository cRepo;
    final PostRepository pRepo;
    @PersistenceContext
    EntityManager em;


    public List<Comment> getComments(Integer postID){

        Post com = em.getReference(Post.class, postID);

        return  cRepo.findAllByPost(com);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsReadOnly(Integer postID){

        Post com = em.getReference(Post.class, postID);

        return cRepo.findAllByPostWithUser(com);
    }

    @Transactional(readOnly = true)
    public List<CommentPaginationDto> getCommentsAsPage(Integer postID, Account viewer){
        Post targetPost = em.getReference(Post.class, postID);
        return cRepo.getCommentsByPost(targetPost, viewer);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Comment create(Post post, String content, Account account) {
        pRepo.updateCommentCount(post.getId(), 1);

        Comment newComment = Comment.builder()
                .author(account)
                .post(post)
                .content(content)
                .build();
        cRepo.save(newComment);
        return newComment;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(Comment answer) {
        pRepo.updateCommentCount(answer.getPost().getId(), -1);
        this.cRepo.delete(answer);
    }




    public Comment getComment(Long id) {
        Optional<Comment> answer = this.cRepo.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("entity(comment) not found");
        }
    }

    public void modify(Comment answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.cRepo.save(answer);
    }





}
