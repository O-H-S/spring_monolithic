package com.ohs.monolithic.utils;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.CommentLike;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.repository.CommentLikeRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.AuthenticationType;
import com.ohs.monolithic.account.domain.LocalCredential;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.account.domain.UserRole;

import com.ohs.monolithic.account.repository.LocalCredentialRepository;
import com.ohs.monolithic.account.repository.OAuth2CredentialRepository;
import groovy.lang.Tuple;
import groovy.lang.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


// Service 레이어를 거치지 않고, DB 데이터 자체의 환경을 구현하기 위함.
@Component
@Transactional
public class RepositoryTestHelper {
  @Autowired
  public LocalCredentialRepository localCredentialRepository;
  @Autowired
  public OAuth2CredentialRepository oAuth2CredentialRepository;
  @Autowired
  public AccountRepository accountRepository;



  @Autowired
  public CommentRepository commentRepository;
  @Autowired
  public BoardRepository boardRepository;
  @Autowired
  public PostRepository postRepository;
  @Autowired
  public CommentLikeRepository commentLikeRepository;


  int dummyMemberCount;
  int dummyBoardCount;
  int dummyPostCount;
  int dummyCommentCount;

  public void resetCounts() {
    dummyMemberCount = 0;
    dummyBoardCount = 0;
    dummyPostCount = 0;
    dummyCommentCount = 0;
  }

  public Tuple3<Post, Account, Board> createPostAccountBoard(){
    Board board = establishBoard();
    Account acc = establishMember();
    Post post = writePostTo(board,acc);

    return Tuple.tuple(post, acc, board);
  }

  public Account establishMember() {
    dummyMemberCount += 1;
    Account newAccount = Account.builder()
            .nickname("dummyUserNick" + dummyMemberCount)
            .authenticationType(AuthenticationType.Local)
            .email("blah{%d}@blah.com".formatted(dummyMemberCount))
            .role(UserRole.USER).build();
    LocalCredential credential = LocalCredential.builder()
            .username("dummyUser" + dummyMemberCount)
            .password("blah blah")
            .account(newAccount)
                    .build();
    accountRepository.save(newAccount);
    localCredentialRepository.save(credential);
    return newAccount;
  }

  public List<Account> establishMembers(int count) {
    List<Account> accounts = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Account newAccount = establishMember();
      accounts.add(newAccount);
    }
    return accounts;
  }

  public Board establishBoard() {
    dummyBoardCount += 1;
    Board newBoard = Board.builder()
            .title("dummyBoard" + dummyBoardCount)
            .description("blah blah")
            .build();
    boardRepository.save(newBoard);
    return newBoard;
  }


  public Post writePostTo(Board targetBoard, Account author) {
    dummyPostCount += 1;
    targetBoard.setPostCount(targetBoard.getPostCount() + 1);
    Post newPost = Post.builder()
            .board(targetBoard)
            .author(author)
            .title("dummyTitle")
            .content("dummyContent")
            .build();
    postRepository.save(newPost);
    boardRepository.save(targetBoard);
    return newPost;
  }

  public Comment writeCommentTo(Post targetPost, Account author) {
    dummyCommentCount += 1;
    targetPost.changeLikeCount(1);
    Comment newComment = Comment.builder()
            .post(targetPost)
            .content("dummyContent")
            .author(author)
            .build();

    postRepository.save(targetPost);
    commentRepository.save(newComment);
    return newComment;
  }

  // 중복 카운팅 되는 것에 주의
  public CommentLike likeTo(Comment targetComment, Account voter){
    targetComment.setLikeCount(targetComment.getLikeCount() + 1);
    CommentLike newCommentLike = CommentLike.builder()
            .comment(targetComment)
            .member(voter)
            .valid(true)
            .build();

    commentLikeRepository.save(newCommentLike);
    commentRepository.save(targetComment);
    return newCommentLike;
  }

  public List<Post> writePostsTo(Board targetBoard) {
    return null;
  }

  public List<Comment> writeCommentsTo(Post targetPost, List<Account> authors, Integer count) {
    List<Comment> comments = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      Account author = authors.get(random.nextInt(authors.size()));
      Comment newComment = writeCommentTo(targetPost, author);
      comments.add(newComment);
    }
    return comments;
  }



  /*public List<Board> establishBoardAndPost(Integer boardCount, Integer postCount){

    //boardRepository.save
  }

  public List<Comment> establishComment(Post targetPost , Integer commentCount){

  }*/


}
