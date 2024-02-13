package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BulkInsertResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.utils.BulkInsertableRepository.BatchProcessor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
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

    /*@Transactional
    public Post create(Integer boardID, Integer userId, String title, String content){
        return create(boardID, )
    }*/

    @Transactional
    public Post create(Integer boardID, PostForm form, Account user) {

        boardService.incrementPostCount(boardID);

        Board boardReference = em.getReference(Board.class, boardID);
        Post q = Post.builder()
                .board(boardReference)
                .author(user)
                .title(form.getSubject())
                .content(form.getContent())
                .build();
        return this.pRepo.save(q);

    }


    @Async("bulkInsertTaskExecutor")
    @Transactional
    public CompletableFuture<Void> createAllAsync(Integer boardID, Long authorId, BiConsumer<Post, Long> entityConfigurer, Long counts){

        Long tID = Thread.currentThread().getId();
        bulkInsertStatus.put(tID, new BulkInsertResponse());

        // 리팩토링 : 이 코드는 내부로 이동시켜야함.
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                //if(status == STATUS_ROLLED_BACK) {
                //System.out.println("rollback_async");
                bulkInsertStatus.remove(tID);
                //}
            }
        });

        boardService.incrementPostCount(boardID, counts.intValue());
        Board boardReference = em.getReference(Board.class, boardID);

        Post reusablePost = Post.builder()
                .board(em.getReference(Board.class, boardID))
                .title("BulkInsertResult")
                .content("BulkInsertResult")
                .author(em.getReference(Account.class, authorId))
                .createDate(LocalDateTime.now())
                .build();

        var customLogic = new BatchProcessor() {
            @Override
            public void process(Integer curBatch, Integer maxBatch, Integer batchSize, Boolean finished, Long executionTime) {

                bulkInsertStatus.get(tID).update(curBatch.longValue() * batchSize, maxBatch.longValue() * batchSize); // 리팩토링 : 이 코드는 내부로 이동시켜야함.
                System.out.println("bulkInsert batch : " + curBatch.toString() + " (" + executionTime.toString() + "ms) ");

            }
        };

        LongFunction<Post> wrappedEntityGenerator = index -> {
            // 기존 entityGenerator 로직
            entityConfigurer.accept(reusablePost, index);
            return reusablePost;
        };
        pRepo.bulkInsert(counts, wrappedEntityGenerator, customLogic);
        return CompletableFuture.completedFuture(null);
    }


    @Transactional
    public void modifyBy(Long id, Account operator, PostForm form){
        // 현재는 form 검증 로직이 컨트롤러에 있어서 상관없지만, 다른 곳에서 이 메소드를 사용한다면 누가 검증?
        Post targetPost = getPostWithReadLock(id);
        if (!targetPost.getAuthor().getId().equals(operator.getId())) {
            throw new RuntimeException("게시글 수정 권한이 없습니다");
        }

        targetPost.setTitle(form.getSubject());
        targetPost.setContent(form.getContent());
        targetPost.setModifyDate(LocalDateTime.now());
        pRepo.save(targetPost);
        // save시 변경된 필드만 업데이트 되나 확인 필요함.
        // 만약 전체를 다 update 시킨다면, 동시에 발생할 수 있는 다른 트랜잭션들과 commentCount, viewCount, likeCount의 값에 불일치가 발생.
        // 변경된 필드(title, content)만 업데이트 시킨다면, 문제가 진짜 없을지 생각해보기.
        // 기본적인 동작은 변경된 컬럼이 하나라도 존재하면(dirty check), 모든 컬럼 update 시킨다.
        // 그러나 @DynamicUpdate를 사용하면 달라짐. (트레이드 오프 존재함)
        // 우선 읽기락으로, Count 값들이 변경되지 못하도록 함.
    }


    @Transactional
    public void delete(Long id){
        Post target = getPostWithWriteLock(id); // s-lock을 걸면, 동시에 삭제할 때 post count가 불일치할 가능성이 존재함.
        Integer boardId = target.getBoard().getId();
        boardService.decrementPostCount(boardId);
        target.setDeleted(Boolean.TRUE);
        pRepo.save(target);
    }

    @Transactional
    public void deleteBy(Long id, Account operator){
        Post targetPost = getPost(id);
        if (!targetPost.getAuthor().getId().equals(operator.getId())) {
            throw new RuntimeException("게시글 삭제 권한이 없습니다");
        }
        delete(id);
    }




    Post getPost(Long id) {
        if(id == null || id < 0)
            throw new IllegalArgumentException("올바르지 않은 post id 값입니다.");

        Optional<Post> postOp = pRepo.findById(id);
        if(postOp.isEmpty())
            throw new DataNotFoundException("존재하지 않는 게시물입니다");
        return postOp.get();
    }

    Post getPostWithWriteLock(Long id) {
        if(id == null || id < 0)
            throw new IllegalArgumentException("올바르지 않은 post id 값입니다.");

        Optional<Post> postOp = pRepo.findByIdWithWriteLock(id);
        if(postOp.isEmpty())
            throw new DataNotFoundException("존재하지 않는 게시물입니다");
        return postOp.get();
    }

    Post getPostWithReadLock(Long id) {
        if(id == null || id < 0)
            throw new IllegalArgumentException("올바르지 않은 post id 값입니다.");

        Optional<Post> postOp = pRepo.findByIdWithReadLock(id);
        if(postOp.isEmpty())
            throw new DataNotFoundException("존재하지 않는 게시물입니다");
        return postOp.get();
    }


}
