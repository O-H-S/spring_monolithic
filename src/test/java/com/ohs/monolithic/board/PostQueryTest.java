package com.ohs.monolithic.board;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@ActiveProfiles("logoff")
public class PostQueryTest {

    @Autowired
    private PostReadService postReadService;
    @Autowired
    private PostWriteService postWriteService;

    @Autowired
    private BoardService boardService;

    //static int targetPage = 30000-2;
    //static int targetPage = 10000-2;
    static int targetPage = 2;
    static int postCount = 300;

    @Test
    //@RepeatedTest(3)
    @DisplayName("offset with SimpleJpaRepository(includes counting)")
    public void queryWithOffsetMethod(){ //

        //given
        BoardResponse testBoard = boardService.createBoard("test", "for test");
        BoardTestUtils.bulkInsert(postWriteService, testBoard.getId(), postCount);


        //when
        long startTime = System.currentTimeMillis();
        Page<Post> results = postReadService.getListLegacy(targetPage, testBoard.getId());
        System.out.println(results.getTotalElements());
        System.out.println(results.getNumberOfElements());
        System.out.println(results.toList().get(0).getId());
        System.out.println("queryWithOffsetMethod() 호출: 실행 시간 = " + (System.currentTimeMillis() - startTime) + "ms");

        //SimpleJpaRepository의 구현에서는 Count 쿼리가 추가된다. (전체 게시글 수를 알아야하기 때문에)

        //then
        assertThat(boardService.getPostCount(testBoard.getId())).as("추가한 개수가 맞는지 확인(캐시)").isEqualTo(postCount);
        //assertThat(postReadService.calculateCount(testBoard.getId())).as("추가한 개수가 맞는지 확인").isEqualTo(postCount);
    }


    @Test
    @RepeatedTest(2)
    @DisplayName("offset without additional Counting")
    public void queryWithoutCounting(){ //

        //given
        BoardResponse testBoard = boardService.createBoard("test", "for test");
        BoardTestUtils.bulkInsert(postWriteService, testBoard.getId(), postCount);
        System.out.println("--------------");

        //when
        long startTime = System.currentTimeMillis();
        Page<Post> results = postReadService.getListWithoutCounting(targetPage, testBoard.getId());
        //System.out.println(results.getTotalElements());
        //System.out.println(results.getNumberOfElements());
        //System.out.println(results.toList().get(0).getId());
        System.out.println("queryWithoutCounting() 호출: 실행 시간 = " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(boardService.getPostCount(testBoard.getId()));
        //SimpleJpaRepository의 구현에서는 Count 쿼리가 추가된다. (전체 게시글 수를 알아야하기 때문에)

        //then
        assertThat(boardService.getPostCount(testBoard.getId())).as("추가한 개수가 맞는지 확인(캐시)").isEqualTo(postCount);
        assertThat(postReadService.calculateCount(testBoard.getId())).as("추가한 개수가 맞는지 확인").isEqualTo(postCount);
    }

    @Test
    @RepeatedTest(2)
    @DisplayName("offset with covering index")
    public void queryWithCoveringIndex(){ //

        //given
        BoardResponse testBoard = boardService.createBoard("test", "for test");
        //for(int i = 0; i < 3; i++)
            BoardTestUtils.bulkInsert(postWriteService, testBoard.getId(), postCount);
        System.out.println("--------------");

        //when
        long startTime = System.currentTimeMillis();
        Page<PostPaginationDto> results = postReadService.getListWithCovering(targetPage, testBoard.getId());
        //System.out.println(results.getTotalElements());
        //System.out.println(results.getNumberOfElements());
        //System.out.println(results.toList().get(0).getId());
        System.out.println("queryWithCoveringIndex() 호출: 실행 시간 = " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(boardService.getPostCount(testBoard.getId()));
        //SimpleJpaRepository의 구현에서는 Count 쿼리가 추가된다. (전체 게시글 수를 알아야하기 때문에)

        //then
        //assertThat(boardManageService.getPostCount(testBoard.getId())).as("추가한 개수가 맞는지 확인(캐시)").isEqualTo(postCount);
        //assertThat(postReadService.calculateCount(testBoard.getId())).as("추가한 개수가 맞는지 확인").isEqualTo(postCount);
    }

    @Test
    //@RepeatedTest(2)
    @DisplayName("no-offset")
    public void queryWithoutOffset(){ //

        //given
        BoardResponse testBoard = boardService.createBoard("test", "for test");
        //for(int i = 0; i < 3; i++)
        BoardTestUtils.bulkInsert(postWriteService, testBoard.getId(), postCount);
        System.out.println("--------------");

        //when
        long startTime = System.currentTimeMillis();
        List<PostPaginationDto> results = postReadService.getListWithoutOffset(250L, testBoard.getId(), 10);
        System.out.println(results.size());
        System.out.println(results.get(0).getId());
        //System.out.println(results.get(9).getId());
        //System.out.println(results.getTotalElements());
        //System.out.println(results.getNumberOfElements());
        //System.out.println(results.toList().get(0).getId());
        System.out.println("queryWithoutOffset() 호출: 실행 시간 = " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.println(boardService.getPostCount(testBoard.getId()));
        //SimpleJpaRepository의 구현에서는 Count 쿼리가 추가된다. (전체 게시글 수를 알아야하기 때문에)

        //then
        //assertThat(boardManageService.getPostCount(testBoard.getId())).as("추가한 개수가 맞는지 확인(캐시)").isEqualTo(postCount);
        //assertThat(postReadService.calculateCount(testBoard.getId())).as("추가한 개수가 맞는지 확인").isEqualTo(postCount);
    }

}
