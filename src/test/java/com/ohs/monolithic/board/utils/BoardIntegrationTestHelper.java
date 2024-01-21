package com.ohs.monolithic.board.utils;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.repository.CommentLikeRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.service.*;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountRepository;
import com.ohs.monolithic.user.AccountService;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

  public Triple<BoardResponse, Account, Post> InitDummy_BoardAccountPost()
  {
    return InitDummy_BoardAccountPost("DummyBoard", "DummyUser", "DummyPost");
  }

  public Triple<BoardResponse, Account, Post> InitDummy_BoardAccountPost(String boardTitle, String userName, String postTitle) {
    BoardResponse newBoard = boardService.createBoard(boardTitle,"Test");
    Account newUser = accountService.create(userName, "abc@naver.com", "blah blah");
    Post post = postWriteService.create(newBoard.getId(), postTitle, "blah blah", newUser);

    return new Triple<>(newBoard, newUser, post);
  }

  //public List<Account> InitDummy_WriteComment(Post targetPost, Integer c)

  public void release(){
    commentLikeRepository.deleteAll();
    commentRepository.deleteAll();
    postRepository.deleteAll();
    accountRepository.deleteAll();
    boardRepository.deleteAll();
  }


}
