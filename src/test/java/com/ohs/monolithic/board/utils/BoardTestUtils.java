package com.ohs.monolithic.board.utils;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.user.Account;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardTestUtils {
  public static List<Board> createBoardList(int numberOfBoards) {
    return IntStream.rangeClosed(1, numberOfBoards)
            .mapToObj(i -> {
              Board newBoard = Board.builder()
                    .title("Test Title " + i)
                    .createDate(LocalDateTime.now().minusHours(i)) // 각각의 보드에 다른 생성 시간을 부여
                    .description("Test Desc. " + i)
                    .build();
              setEntityID(newBoard, "id", i);
              return newBoard;
            })
            .collect(Collectors.toList());
  }

  public static Board createBoardSimple(Integer id, String title) {
    return createBoardSimple(id, title, "Test Description");
  }

  public static Board createBoardSimple(Integer id, String title, String desc) {
    Board newBoard =Board.builder()
            .title(title)
            .description(desc)
            .build();
    setEntityID(newBoard, "id", 1L);
    return newBoard;
  }

  public static List<Post> bulkInsert(PostWriteService writeService, Integer boardID, int count) {
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
    writeService.createAll(boardID, posts);
    return posts;
  }

  public static Triple<Post, Account, Board> createSimplePostAccountBoard() {
    Board simepleBoard = createBoardSimple(1, "Test", "Test");

    Account simpleAccount = Account.builder()
            .build();
    setEntityID(simpleAccount, "id", 1L);

    Post simplePost = Post.builder()
            .author(simpleAccount)
            .board(simepleBoard)
            .build();
    setEntityID(simplePost, "id", 1);

    return new Triple<>(simplePost, simpleAccount, simepleBoard);
  }

  static void setEntityID(Object targetObject, String fieldName, Object value) {
    try {
      Field field = targetObject.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);  // private 필드 접근을 위해 필요
      field.set(targetObject, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


}