package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BulkInsertResponse;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.utils.JdbcOperationsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongFunction;


@RequiredArgsConstructor
@Service
public class PostWriteService {
    @PersistenceContext
    EntityManager em;
    final PostRepository pRepo;



    /*
        (ToDo)
        bulkInsert 관련 로직은 별도의 빈으로 주입 받도록 변경 하는게 좋아보임.(Service에서 많은 책임을 가지고 있는듯? ex.BulkInsertManager)
        JdbcOperationsRepository.BatchProcessor 익명 클래스를 Service에서 조작하는 것이 바람직한가?
    */

    //@Qualifier("bulkInsertTaskExecutor")
    // lombok의 constructor 자동 생성에서 @Qualifier를 자동 생성하지 않는다.
    // 변수명을 빈의 이름으로 바꾸거나, 별도의 설정을 추가하면된다. (이름 바꾸는 것이 더 간단해서 이렇게함)
    private final Executor bulkInsertTaskExecutor;
    private final ConcurrentHashMap<Long, BulkInsertResponse> bulkInsertStatus= new ConcurrentHashMap<>();; // long은 값 타입이므로 key로 설정 불가능하다?



    private final BoardService boardService;

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

        Board boardReference = em.getReference(Board.class, boardID);
        Post q = Post.builder()
                .board(boardReference)
                .author(user)
                .title(subject)
                .content(content)
                .build();
        return this.pRepo.save(q);

    }

    private void _createAll(Integer boardID, List<Post> posts, LongFunction<Post> entityGenerator, Long counts, JdbcOperationsRepository.BatchProcessor logicPerBatch){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if(status == STATUS_ROLLED_BACK) {
                    System.out.println("rollback");
                    boardService.incrementPostCount(boardID, -counts.intValue());
                }
            }
        });
        boardService.incrementPostCount(boardID, counts.intValue());
        //System.out.println(String.format("%d, %d", posts.size(), boardService.getPostCount(boardID)));
        Board boardReference = em.getReference(Board.class, boardID);

        if(entityGenerator == null) {
            posts.forEach(post -> post.setBoard(boardReference));
            pRepo.bulkInsert(posts, logicPerBatch);
        }
        else {
            LongFunction<Post> wrappedEntityGenerator = index -> {
                // 기존 entityGenerator 로직
                Post generated = entityGenerator.apply(index);
                generated.setBoard(boardReference);
                return generated;
            };
            pRepo.bulkInsert(counts, wrappedEntityGenerator, logicPerBatch);
        }
    }

    @Transactional
    public void createAll(Integer boardID, List<Post> posts) {
        this._createAll(boardID, posts, null, (long)posts.size(), null);
    }


    @Async("bulkInsertTaskExecutor")
    @Transactional
    public void createAllAsync(Integer boardID, List<Post> posts, LongFunction<Post> entityGenerator, Long counts){
        Long tID = Thread.currentThread().getId();
        bulkInsertStatus.put(tID, new BulkInsertResponse());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                //if(status == STATUS_ROLLED_BACK) {
                //System.out.println("rollback_async");
                bulkInsertStatus.remove(tID);
                //}
            }
        });


        this._createAll(boardID, posts, entityGenerator, posts != null ? (long)posts.size() : counts ,new JdbcOperationsRepository.BatchProcessor() {
            @Override
            public void process(Integer curBatch, Integer maxBatch, Integer batchSize ,Boolean finished, Long executionTime) {

                bulkInsertStatus.get(tID).update(curBatch.longValue() * batchSize, maxBatch.longValue() * batchSize);
                System.out.println("bulkInsert batch : " + curBatch.toString() + " (" + executionTime.toString() + "ms) ");
               /* bulkInsertStatus.compute(tID, (aLong, bulkInsertResponse) -> {
                    bulkInsertResponse.update(curBatch.longValue() * batchSize, maxBatch.longValue() * batchSize);
                    return bulkInsertResponse;
                });*/
            }

        });

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
