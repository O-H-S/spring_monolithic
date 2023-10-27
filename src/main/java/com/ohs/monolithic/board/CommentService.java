package com.ohs.monolithic.board;


import com.ohs.monolithic.board.exception.DataNotFoundException;
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

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void create(Post post, String content, Account account) {
        pRepo.updateCommentCount(post.getId(), 1);

        Comment answer = new Comment();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setPost(post);
        answer.setAuthor(account);
        cRepo.save(answer);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(Comment answer) {
        pRepo.updateCommentCount(answer.getPost().getId(), -1);
        this.cRepo.delete(answer);
    }




    public Comment getComment(Integer id) {
        Optional<Comment> answer = this.cRepo.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Comment answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.cRepo.save(answer);
    }

    public void vote(Comment answer, Account siteUser) {
        answer.getVoter().add(siteUser);
        this.cRepo.save(answer);
    }



}
