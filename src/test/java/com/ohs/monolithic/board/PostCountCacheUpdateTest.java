package com.ohs.monolithic.board;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.service.BoardInternalService;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.board.service.PostReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostCountCacheUpdateTest {

    @Autowired
    private PostWriteService postWriteService;
    @Autowired
    private PostReadService postReadService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardInternalService boardInternalService;

    @Test
    @DisplayName("비동기적 Post 생성 후, Cache 값과 실제 데이터베이스의 Post 레코드 수가 일치해야한다.")
    public void testCountCache() throws InterruptedException {

        // given
        BoardResponse board =  boardService.createBoard("test", "test");
       // BoardResponse board2 =  boardManageService.createBoard("test2", "test");



        // when

        int threadCount = 50;
        int postsPerBoardToCreate = 30; // N
        int postsPerBoardToDelete = 5; // M, M < N

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {

                //List<Post> createdPosts = Collections.synchronizedList(new ArrayList<>());
                for (int j = 0; j < postsPerBoardToCreate; j++) {
                    PostDetailResponse post = postWriteService.create(board.getId(), PostForm.builder().content("abc").subject("abc").build(), null);
                    //System.out.println( String.format("%d", threadIndex));
                    //createdPosts.add(post);
                }
            });
        }
        executorService.shutdown();
        boolean finished = executorService.awaitTermination(3L, TimeUnit.MINUTES);

        // then
        if (!finished) {
            throw new RuntimeException("테스트 실행 시간 초과");
        }

        System.out.println(boardInternalService.getPostCount(board.getId()));
        System.out.println(postReadService.calculateCount(board.getId()));

        // 10개의 쓰레드가 비동기적으로 각각 N개의 Post를 board에 create하고
        // 나머지 10개의 쓰레드가 비동기적으로 각각 M개의 Post를 board에서 delete함. (N > M)
        /*Post firstPost = postWriteService.create(response.id, "abc", "abc", null);
        postWriteService.create(response.id, "abc", "abc", null);
        postWriteService.create(response.id, "abc", "abc", null);

        postWriteService.create(response2.id, "abc", "abc", null);

        System.out.println(boardManageService.getPostCount(response.id));
        System.out.println(boardManageService.getPostCount(response2.id));

        postWriteService.delete(firstPost);
        System.out.println(boardManageService.getPostCount(response.id));
        postWriteService.delete(firstPost);
        System.out.println(boardManageService.getPostCount(response.id));*/
        /*Integer boardId = 1; // 테스트를 위한 보드 ID
        String subject = "Test Subject";
        String content = "Test Content";
        Account user = new Account(); // 테스트를 위한 사용자 계정

        // 캐시 초기화
        ConcurrentHashMap<Integer, Long> cache = new ConcurrentHashMap<>();
        boardManageService.registerPostCountCache(cache);

        // 포스트 생성
        postWriteService.create(boardId, subject, content, user);

        // 캐시와 DB 동기화 확인
        long postCountInCache = cache.getOrDefault(boardId, 0L);
        long postCountInDb = postRepository.countByBoardId(boardId);
        assertEquals(postCountInCache, postCountInDb);

        // 포스트 삭제
        Post post = postRepository.findFirstByBoardId(boardId);
        postWriteService.delete(post);

        // 캐시와 DB 동기화 재확인
        postCountInCache = cache.getOrDefault(boardId, 0L);
        postCountInDb = postRepository.countByBoardId(boardId);
        assertEquals(postCountInCache, postCountInDb);*/
    }
}