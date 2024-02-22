package com.ohs.monolithic.board.repository;


import com.ohs.monolithic.common.configuration.QuerydslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@Tag("base")
@Tag("integrate-limited")
public class PostRepositoryTest {

    @Autowired // datajpatest 어노테이션이 관련 context를 로드하기 때문에 가능하다.
    PostRepository postRepository;

    @Test
    @DisplayName("bulkInsert(counts, generator, batchProcessor) : 동적으로 Post를 가져오며 insert 가능")
    public void creatingBoard(){
        // given


        // when

        /*Post reusablePost = Post.builder()
                .title("Test title")
                .author(null)
                .createDate(LocalDateTime.now())
                .build();*/

        /*postRepository.bulkInsert(100L, idx -> {
            reusablePost.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx+1, form.getCount(), System.currentTimeMillis()-startTime ));
            return reusablePost;
        });*/

        // then


    }

}
