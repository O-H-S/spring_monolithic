package com.ohs.monolithic.problem.service;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.domain.constants.PostTagType;
import com.ohs.monolithic.board.event.PostDeleteEvent;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostPaginationService;
import com.ohs.monolithic.board.service.PostTagService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.ProblemPostCreateEvent;
import com.ohs.monolithic.problem.domain.ProblemPostHistory;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.dto.ProblemPostCreationResponse;
import com.ohs.monolithic.problem.dto.ProblemPostPaginationResponse;
import com.ohs.monolithic.problem.repository.ProblemPostHistoryRepository;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


// 문제에 관련된 게시글은 문제 Tag를 붙혀서 식별함.
@Service
@RequiredArgsConstructor
public class ProblemPostService {
  final PostTagService postTagService;
  final private ProblemRepository problemRepository;
  final private ProblemPostHistoryRepository problemPostHistoryRepository;
  final private PostWriteService postWriteService;
  final private BoardService boardService;
  final private PostPaginationService postPaginationService;

  final public static String POST_TAG_PREFIX = "P";
  final public static String PROBLEM_BOARD_NAME = "Problem";
  Integer problemBoardId;

  @EventListener(ApplicationReadyEvent.class)
  public void doSomethingAfterStartup() {
    Board pBoard = boardService.getBoardFromTitle(PROBLEM_BOARD_NAME);
    if(pBoard == null)
      problemBoardId = boardService.createBoard(PROBLEM_BOARD_NAME, "").getId();
    else
      problemBoardId = pBoard.getId();
  }


  @Transactional
  public ProblemPostCreationResponse createProblemPost(Long problemId, PostForm postForm, AppUser user){
    Optional<Problem> problemOp = problemRepository.findProblemWithLock(problemId);
    if(problemOp.isEmpty())
      throw new EntityNotFoundException("Problem not found");
    Problem problem = problemOp.get();

    Post post = postWriteService.createWithEvent(problemBoardId, postForm, user, new ProblemPostCreateEvent(problem));
    PostTag metaTag = postTagService.addPostTag(post.getId(), POST_TAG_PREFIX + problemId.toString(), PostTagType.System);
    problem.setPostCount(problem.getPostCount() + 1);
    problemRepository.save(problem);

    ProblemPostCreationResponse response = new ProblemPostCreationResponse();
    response.setProblemData(ProblemPaginationDto.of(problem, null));
    response.setPostData(PostDetailResponse.of(post, Boolean.TRUE, Boolean.FALSE, Collections.singletonList(metaTag)) );




    return response;
  }


  //@EventListener
  //@TransactionalEventListener

  // 이미 트랜잭션이 커밋되었기에 변경을 다시 할 수는 없으므로, Propagation.REQUIRES_NEW 를 통해 기존의(커밋을 완료한) 트랜잭션에 참여시키지 않고, 새롭게 생성.
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePostCreated(ProblemPostCreateEvent event) {
    Post newPost = event.getPost();
    ProblemPostHistory history = new ProblemPostHistory();
    history.setPost(newPost);
    history.setCreateDate(newPost.getCreateDate());
    history.setValid(Boolean.TRUE);
    history.setProblem(event.getProblem());
    problemPostHistoryRepository.save(history);

  }
  @EventListener
  public void handlePostDeleted(PostDeleteEvent event) {
    List<String> tagNames = postTagService.getTagNamesFromPost(event.getPost().getId());
    if(tagNames.isEmpty())
      return;

    for(String tag : tagNames){
      if(tag.startsWith(POST_TAG_PREFIX)){

        try {
          Long problemId = Long.parseLong(tag.replace(POST_TAG_PREFIX, ""));
          Optional<Problem> problemOp = problemRepository.findProblemWithLock(problemId);
          if(problemOp.isEmpty())
            continue;
          Problem problem = problemOp.get();
          problem.setPostCount(problem.getPostCount() -1);
          problemRepository.save(problem);

        }catch (Exception e) {
          continue;
        }
      }
    }

  }

  @Transactional(readOnly = true)
  public ProblemPostPaginationResponse getProblemPosts(Long problemId, Integer page, Integer pageSize){
    Page<PostPaginationDto> result = postPaginationService.getListWithCovering( page,problemBoardId, pageSize, Collections.singletonList("P" + problemId));

    ProblemPostPaginationResponse response = new ProblemPostPaginationResponse();
    response.setData(result.getContent());
    response.setTotalCounts(result.getTotalElements());
    response.setTotalPages((long)result.getTotalPages());
    return response;
  }

}
