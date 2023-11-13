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

    private final BoardManageService boardService;

    @Transactional
    public Post create(Integer boardID ,String subject, String content, Account user) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if(status == STATUS_ROLLED_BACK)
                    boardService.decrementPostCount(boardID);
            }
        });
        boardService.incrementPostCount(boardID);
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


    @Transactional
    public void delete(Post post) {
        Optional<Post> entity = pRepo.findById(post.getId());
        if(entity.isEmpty())
            return;

        Integer boardId = entity.get().getId();
        boardService.decrementPostCount(boardId);
        // 트랜잭션 커밋 시 실행될 콜백
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if(status == STATUS_ROLLED_BACK)
                    boardService.incrementPostCount(boardId);
            }
        });

        pRepo.delete(post);
    }




    public void vote(Post post, Account siteUser) {
        post.getVoter().add(siteUser);
        this.pRepo.save(post);
    }

}