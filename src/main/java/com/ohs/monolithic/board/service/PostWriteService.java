package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.user.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PostWriteService {
    @PersistenceContext
    EntityManager em;
    final PostRepository pRepo;
    public void create(Integer boardID ,String subject, String content, Account user) {
        Post q = new Post();
        Board boardReference = em.getReference(Board.class, boardID);
        q.setBoard(boardReference);
        q.setTitle(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.pRepo.save(q);
    }

    public void modify(Post post, String title, String content) {
        post.setTitle(title);
        post.setContent(content);
        post.setModifyDate(LocalDateTime.now());
        this.pRepo.save(post);
    }

    public void delete(Post post) {
        this.pRepo.delete(post);
    }
    public void vote(Post post, Account siteUser) {
        post.getVoter().add(siteUser);
        this.pRepo.save(post);
    }

}
