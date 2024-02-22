package com.ohs.monolithic.board;


import com.ohs.monolithic.common.configuration.QuerydslConfig;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.account.domain.Account;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(/*showSql = false*/)
//@ActiveProfiles("logoff")
/*@TestPropertySource(properties = {
        "logging.level.org.hibernate.SQL=OFF",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=OFF"
})*/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(QuerydslConfig.class)
public class PostInsertPerformanceTest {
    @Autowired
    PostRepository repository;
    @Autowired
    private EntityManager entityManager;
    static Account testAccount;
    static List<Post> posts;
    static int postCount = 5000;
    @BeforeAll // 전체 테스트를 시작하기 전에 1회 실행하므로 메서드는 static으로 선언
    static void beforeAll() {
        //System.out.println("@BeforeAll");



    }

    @BeforeEach
    public void beforeEach() {
        System.out.println("_____________________________________________________");
        System.out.println("[beforeEach]");
        entityManager.clear(); // 영속성 컨텍스트 초기화
        repository.deleteAllInBatch(); // 특별한 설정이 없으면, 롤백되긴 하지만 혹시 모르니 추가해둠. @Rollback(false)을 설정하면, 필요해질수있다.

        posts = new ArrayList<>();
        for (int i = 0; i < postCount; i++) {
            posts.add(Post.builder()
                    .title("TestTitle")
                    .author(null)
                    .createDate(LocalDateTime.now())
                    .content("TestContent")
                    .build()
            );
        }
        System.out.println("----------");
    }

    @AfterEach // 테스트 케이스를 종료하기 전마다 실행
    public void afterEach() {
        long actualSize = repository.count();
        assertThat(actualSize).as("추가한 개수가 맞는지 확인").isEqualTo(postCount);

    }

    /*@Test
    @Order(0)
    @DisplayName("preperation")
    //@RepeatedTest(2)
    public void insert_TestPreperation(){
        repository.save(posts.get(0));
        entityManager.flush();
    }*/


    @Test
    @Order(1)
    @DisplayName("save() 반복 호출")
    @RepeatedTest(5)
    //@RepeatedTest(2)
    public void insert_saveIteration(){


        // given
            // beforeAll에 정의되어 있음.
        // when
        long startTime = System.currentTimeMillis();
        posts.forEach(post -> repository.save(post));

        // IDENTITY 전략이기 때문에, 매 save마다 실제 insert 쿼리가 바로 반영이 됨. (즉, postCount만큼)
        //long beforeFlush_Size = repository.count();
        //System.out.println(beforeFlush_Size);

        //entityManager.flush();
        long endTime = System.currentTimeMillis();
        // then
            // afterEach에 정의되어 있음.
        System.out.println("save() 호출: 실행 시간 = " + (endTime - startTime) + "ms");
    }

    @Test
    @Order(2)
    @DisplayName("saveAll() 호출")
    @RepeatedTest(5)
    //@RepeatedTest(2)
    public void insert_saveAll(){
        // given
        // beforeAll에 정의되어 있음.

        // when
        long startTime = System.currentTimeMillis();
        repository.saveAll(posts);
        //entityManager.flush();
        long endTime = System.currentTimeMillis();

        // then
        // afterEach에 정의되어 있음.
        System.out.println("saveAll() 호출: 실행 시간 = " + (endTime - startTime) + "ms");
    }

    @Test
    @Order(3)
    @DisplayName("jdbc batch 사용")
    @RepeatedTest(5)
    public void insert_jdbcBatch(){
        // given
        // beforeAll에 정의되어 있음.

        // when
        long startTime = System.currentTimeMillis();
        repository.bulkInsert(posts);
        long endTime = System.currentTimeMillis();

        // then
        // afterEach에 정의되어 있음.
        System.out.println("jdbcBatch() 호출: 실행 시간 = " + (endTime - startTime) + "ms");
    }

}
