package com.ohs.monolithic;

import com.ohs.monolithic.board.Comment;
import com.ohs.monolithic.board.CommentService;
import com.ohs.monolithic.board.Post;
import com.ohs.monolithic.board.read.PostReadService;
import com.ohs.monolithic.board.write.PostWriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
class MonolithicApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private PostWriteService postWriteService;
	@Autowired
	private PostReadService postReadService;

	@Autowired
	private CommentService commentService;


	@Test
	void testJpa() {
		for (int i = 1; i <= 100; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			this.postWriteService.create(1, subject, content, null);
		}
	}


	@Test
	@Transactional
	void commentCountTest() throws InterruptedException, ExecutionException {

		Post targetPost =  postReadService.getPost(2, true);

		System.out.println("target Post:");
		System.out.println(targetPost.getCommentCount());
		System.out.println(targetPost.getContent());
		System.out.println(targetPost.getCommentList());
		targetPost.setCommentCount(targetPost.getCommentList().size());

		postReadService.test();
		/*ExecutorService executorService = Executors.newFixedThreadPool(2);

		// 첫 번째 작업: create 메서드 호출
		Callable<Boolean> createTask = () -> {
			commentService.create(post, "testContent", account);
			return true;
		};

		// 두 번째 작업: delete 메서드 호출
		// 이 예제에서는 미리 생성된 Comment 객체를 사용하였습니다.
		Comment existingComment = new Comment();  // 미리 로드 또는 생성해야 함
		Callable<Boolean> deleteTask = () -> {
			commentService.delete(existingComment);
			return true;
		};

		// 작업 실행 및 결과 기다리기
		Future<Boolean> createFuture = executorService.submit(createTask);
		Future<Boolean> deleteFuture = executorService.submit(deleteTask);

		// 결과 확인
		*//*assertTrue(createFuture.get());
		assertTrue(deleteFuture.get());*//*

		// ExecutorService 종료
		executorService.shutdown();*/







	}

}
