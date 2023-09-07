package com.ohs.monolithic;

import com.ohs.monolithic.board.write.PostWriteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MonolithicApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private PostWriteService postWriteService;

	@Test
	void testJpa() {
		for (int i = 1; i <= 100; i++) {
			String subject = String.format("테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			this.postWriteService.create(1, subject, content);
		}
	}

}
