package com.ohs.monolithic.utils;


import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.account.service.LocalAccountService;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
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
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import com.ohs.monolithic.problem.service.ProblemService;
import org.antlr.v4.runtime.misc.Triple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class IntegrationTestHelper {
  @Autowired
  public BoardInternalService boardInternalService;
  @Autowired
  public BoardService boardService;
  @Autowired
  public AccountService accountService;
  @Autowired
  public LocalAccountService localAccountService;

  @Autowired
  public BoardPermissionService boardPermissionService;

  @Autowired
  public PostWriteService postWriteService;
  @Autowired
  public PostReadService postReadService;

  @Autowired
  public PostLikeService postLikeService;
  @Autowired
  public PostTagService postTagService;


  @Autowired
  public CommentService commentService;
  @Autowired
  public CommentLikeService commentLikeService;

  @Autowired
  public ProblemService problemService;

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

  @Autowired
  PostTagRepository postTagRepository;

  @Autowired
  ProblemRepository problemRepository;

  /*RedisTemplate<String, String>의 편의성 버전입니다.
  키와 값 모두 자동으로 String으로 처리되도록 사전 설정되어 있습니다.*/

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  public Triple<BoardResponse, Account, PostDetailResponse> InitDummy_BoardAccountPost()
  {
    return InitDummy_BoardAccountPost("DummyBoard", "DummyUser", "DummyPost");
  }

  public Triple<BoardResponse, Account, PostDetailResponse> InitDummy_BoardAccountPost(String boardTitle, String userName, String postTitle) {
    BoardResponse newBoard = boardService.createBoard(boardTitle,"Test");
    boardPermissionService.addWritePermission(newBoard.getId(), UserRole.USER, "*");
    Account newUser = accountService.createAsLocal(userName, "test@abc.ddd", userName, "blah");


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


  public BoardResponse simpleBoard()
  {
    return simpleBoard(null);
  }
  public BoardResponse simpleBoard(BoardPaginationType paginationType){
    Random random = new Random();
    int randomValue = random.nextInt();

    BoardResponse newBoard = boardService.createBoard("dummyBoard" + randomValue,"", paginationType);
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
  public List<Problem> simpleProblem(String platform, String title, Integer count){

    return simpleProblem(platform, title, null, null,null,count);
  }
  public List<Problem> simpleProblem(String platform, String title, String difficulty, Float level, Consumer<Problem> customizer, Integer count){
    List<Problem> problems = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      int randomValue = random.nextInt();
      Problem newProblem = Problem.builder()
              .platform(platform)
              .title(title + i)
              .platformId(String.valueOf(randomValue))
              .version(0)
              .difficulty(difficulty != null? difficulty : String.valueOf(random.nextInt(100)))
              //.difficulty(String.valueOf(random.nextInt(100)))
              .level(level != null ? level : random.nextFloat(5.0f))
              .foundDate(LocalDateTime.now())
              .link("https://test.com")
              .build();

      if(customizer != null)
        customizer.accept(newProblem);

      problemRepository.save(newProblem);
      problems.add(newProblem);
    }
    return problems;
  }


  //public List<Account> InitDummy_WriteComment(Post targetPost, Integer c)

  public void release(){
    System.out.println("---------------helper.release-------------");


    // 로컬 레디스 정리.
    Set<String> keys = stringRedisTemplate.keys("*");
    if (keys != null && !keys.isEmpty()) {
      stringRedisTemplate.delete(keys);
    }

    WithMockCustomUserContext.clear();


    // 외래키 제약으로 인해, delete의 순서가 중요하다. (에러 발생함)
    // TODO : trancate 명령어 사용하기.
    problemRepository.deleteAll();

    postTagRepository.deleteAll();
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
