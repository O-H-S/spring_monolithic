package com.ohs.monolithic.utils;


import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.repository.*;
import com.ohs.monolithic.board.service.*;
import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.account.repository.LocalCredentialRepository;
import com.ohs.monolithic.account.repository.OAuth2CredentialRepository;
import com.ohs.monolithic.account.service.AccountService;
import org.antlr.v4.runtime.misc.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IntegrationTestHelper {
  @Autowired
  public BoardService boardService;
  @Autowired
  public AccountService accountService;

  @Autowired
  public BoardPermissionService boardPermissionService;

  @Autowired
  public PostWriteService postWriteService;
  @Autowired
  public PostReadService postReadService;

  @Autowired
  public PostLikeService postLikeService;


  @Autowired
  public CommentService commentService;
  @Autowired
  public CommentLikeService commentLikeService;

  // repos
  @Autowired
  CommentLikeRepository commentLikeRepository;
  @Autowired
  CommentRepository commentRepository;
  @Autowired
  PostRepository postRepository;

  @Autowired
  public LocalCredentialRepository localCredentialRepository;
  @Autowired
  public OAuth2CredentialRepository oAuth2CredentialRepository;

  @Autowired
  AccountRepository accountRepository;
  @Autowired
  BoardRepository boardRepository;
  @Autowired
  BoardPermissionRepository boardPermissionRepository;
  @Autowired
  PostViewRepository postViewRepository;
  @Autowired
  PostLikeRepository postLikeRepository;


  public Triple<BoardResponse, Account, PostDetailResponse> InitDummy_BoardAccountPost()
  {
    return InitDummy_BoardAccountPost("DummyBoard", "DummyUser", "DummyPost");
  }

  public Triple<BoardResponse, Account, PostDetailResponse> InitDummy_BoardAccountPost(String boardTitle, String userName, String postTitle) {
    BoardResponse newBoard = boardService.createBoard(boardTitle,"Test");
    boardPermissionService.addWritePermission(newBoard.getId(), UserRole.USER, "*");
    Account newUser = accountService.createAsLocal(userName,"test@abc.ddd", userName, "blah");

    PostForm newPostForm = PostForm.builder()
                    .subject("hello")
                            .content("my name is oh")
                                    .build();


    PostDetailResponse post = postWriteService.create(newBoard.getId(), newPostForm, getDummyAppUser(newUser));

    return new Triple<>(newBoard, newUser, post);
  }

  LocalAppUser getDummyAppUser(Account account){
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority(UserRole.USER.toString()));

    LocalAppUser appUser = new LocalAppUser(account.getId(), account.getNickname(), "blah", account.getNickname(), authorities);
    return appUser;
  }


  public BoardResponse simpleBoard(){
    Random random = new Random();
    int randomValue = random.nextInt();

    BoardResponse newBoard = boardService.createBoard("dummyBoard" + randomValue,"");
    boardPermissionService.addWritePermission(newBoard.getId(), UserRole.USER, "*");
    return newBoard;
  }


  public Account simpleAccount(){
    Random random = new Random();
    int randomValue = random.nextInt();

    Account newUser = accountService.createAsLocal("dummyNick" + randomValue, "dummyEmail" + randomValue, "dummy" + randomValue, "blah blah");
    return newUser;
  }

  public List<PostDetailResponse> simplePost(Integer boardId, Account writer ,Integer count){
    Random random = new Random();
    List<PostDetailResponse> posts = new ArrayList<>();
    if(writer == null)
      writer = simpleAccount();



    for (int i = 0; i < count; i++) {
      int randomValue = random.nextInt();

      PostForm newPostForm = PostForm.builder()
              .subject("dummy(" + i +")"+ randomValue)
              .content("my name is oh")
              .build();



      PostDetailResponse post = postWriteService.create(boardId, newPostForm, getDummyAppUser(writer));
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
    postLikeRepository.deleteAll();
    postViewRepository.deleteAll();
    commentLikeRepository.deleteAll();
    commentRepository.deleteAll();
    postRepository.deleteAll();

    localCredentialRepository.deleteAll();
    oAuth2CredentialRepository.deleteAll();
    accountRepository.deleteAll();
    boardPermissionRepository.deleteAll();
    boardRepository.deleteAll();
    System.out.println("--------------------------------------------");
  }


}
