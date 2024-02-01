package com.ohs.monolithic.board.utils;


import com.ohs.monolithic.board.BoardPaginationType;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.repository.*;
import com.ohs.monolithic.board.service.*;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountRepository;
import com.ohs.monolithic.user.AccountService;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.random.RandomGenerator;

@Component
public class BoardIntegrationTestHelper {
  @Autowired
  public BoardService boardService;
  @Autowired
  public AccountService accountService;
  @Autowired
  public PostWriteService postWriteService;
  @Autowired
  public PostReadService postReadService;

  @Autowired
  public CommentService commentService;
  @Autowired
  public CommentLikeService commentLikeService;

  @Autowired
  CommentLikeRepository commentLikeRepository;
  @Autowired
  CommentRepository commentRepository;
  @Autowired
  PostRepository postRepository;
  @Autowired
  AccountRepository accountRepository;
  @Autowired
  BoardRepository boardRepository;
  @Autowired
  PostViewRepository postViewRepository;

  public Triple<BoardResponse, Account, Post> InitDummy_BoardAccountPost()
  {
    return InitDummy_BoardAccountPost("DummyBoard", "DummyUser", "DummyPost");
  }

  public Triple<BoardResponse, Account, Post> InitDummy_BoardAccountPost(String boardTitle, String userName, String postTitle) {
    BoardResponse newBoard = boardService.createBoard(boardTitle,"Test");
    Account newUser = accountService.create(userName, "abc@naver.com", "blah blah");
    Post post = postWriteService.create(newBoard.getId(), PostForm.builder().subject("abc").content("abc").build() , newUser);

    return new Triple<>(newBoard, newUser, post);
  }

  public Account simpleAccount(){
    Random random = new Random();
    int randomValue = random.nextInt();

    Account newUser = accountService.create("dummy" + randomValue, "abc@naver.com", "blah blah");
    return newUser;
  }

  public List<Post> simplePost(Integer boardId, Account writer ,Integer count){
    Random random = new Random();
    List<Post> posts = new ArrayList<>();
    if(writer == null)
      writer = simpleAccount();
    for (int i = 0; i < count; i++) {
      int randomValue = random.nextInt();
      Post post = postWriteService.create(boardId, PostForm.builder().subject("dummy(" + i +")"+ randomValue).content("abc").build(), writer);
      posts.add(post);
    }

    return posts;
  }


  //public List<Account> InitDummy_WriteComment(Post targetPost, Integer c)

  public void release(){
    System.out.println("---------------helper.release-------------");
    boardService.registerPostCountCache(new ConcurrentHashMap<Integer, Long>());

    // 외래키 제약으로 인해, delete의 순서가 중요하다. (에러 발생함)
    // TODO : trancate 명령어 사용하기.
    postViewRepository.deleteAll();
    commentLikeRepository.deleteAll();
    commentRepository.deleteAll();
    postRepository.deleteAll();
    accountRepository.deleteAll();
    boardRepository.deleteAll();
    System.out.println("--------------------------------------------");
  }


}
