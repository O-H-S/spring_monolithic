package com.ohs.monolithic.board;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.service.PostWriteService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardTestUtils {
    public static List<Board> createBoardList(int numberOfBoards) {
        return IntStream.rangeClosed(1, numberOfBoards)
                .mapToObj(i -> Board.builder()
                        .id(i)
                        .title("Test Title " + i)
                        .createDate(LocalDateTime.now().minusHours(i)) // 각각의 보드에 다른 생성 시간을 부여
                        .postCount(0L)
                        .description("Test Desc. " + i)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<Post> bulkInsert(PostWriteService writeService, Integer boardID, int count){
        List<Post> posts = new ArrayList<>();
        LocalDateTime cdate = LocalDateTime.now();
        for (int i = 0; i < count; i++) {
            posts.add(Post.builder()
                    .title("TestTitle")
                    .author(null)
                    .createDate(cdate)
                    .content("TestContent")
                    .build()
            );
            cdate = cdate.plusSeconds(1);
        }
        writeService.createAll(boardID , posts);
        return posts;
    }
}