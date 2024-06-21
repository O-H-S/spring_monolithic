package com.ohs.monolithic.problem.service;


import com.ohs.monolithic.account.repository.AccountNotificationRepository;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.account.service.AccountNotificationService;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.ProblemBookmark;
import com.ohs.monolithic.problem.domain.ProblemPostHistory;
import com.ohs.monolithic.problem.domain.ProblemPostNotification;
import com.ohs.monolithic.problem.dto.ProblemBookmarkCreationForm;
import com.ohs.monolithic.problem.dto.ProblemBookmarkMutationResponse;
import com.ohs.monolithic.problem.repository.ProblemBookmarkRepository;
import com.ohs.monolithic.problem.repository.ProblemPostHistoryRepository;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemBookmarkService {

  final ProblemBookmarkRepository problemBookmarkRepository;
  final ProblemPostHistoryRepository problemPostHistoryRepository;
  final ProblemRepository problemRepository;
  final AccountRepository accountRepository;
  final AccountNotificationService notificationService;
  final AccountNotificationRepository accountNotificationRepository;

  @Transactional
  public ProblemBookmarkMutationResponse bookmarkProblem(AppUser user, Long problemId, ProblemBookmarkCreationForm form){

    Problem problemRef = problemRepository.getReferenceById(problemId);
    Optional<ProblemBookmark> bookmarkOp = problemBookmarkRepository.findByAccountAndProblem(accountRepository.getReferenceById( user.getAccountId()), problemRef);

    ProblemBookmarkMutationResponse response = new ProblemBookmarkMutationResponse();

    ProblemBookmark bookmark;
    if(bookmarkOp.isEmpty()) {
      bookmark = new ProblemBookmark();
      bookmark.setProblem(problemRef);
      bookmark.setAccount(accountRepository.getReferenceById(user.getAccountId()));
      bookmark.setBookmarkType(form.getType());
      response.setChanged(true);
    }
    else {
      bookmark = bookmarkOp.get();
      response.setChanged(bookmark.getBookmarkType() != form.getType());
      bookmark.setBookmarkType(form.getType());
    }

    problemBookmarkRepository.save(bookmark);
    return response;
  }
  
  @Transactional
  public ProblemBookmarkMutationResponse unbookmarkProblem(AppUser user, Long problemId){
    Problem problemRef = problemRepository.getReferenceById(problemId);
    Optional<ProblemBookmark> bookmarkOp = problemBookmarkRepository.findByAccountAndProblem(accountRepository.getReferenceById( user.getAccountId()), problemRef);
    
    ProblemBookmarkMutationResponse response = new ProblemBookmarkMutationResponse();

    ProblemBookmark bookmark;
    if(bookmarkOp.isEmpty()) {
      response.setChanged(false);
    }
    else {
      bookmark = bookmarkOp.get();
      response.setChanged(bookmark.getBookmarkType() != null);
      bookmark.setBookmarkType(null);
      problemBookmarkRepository.save(bookmark);
    }
    return response;
  }


  @EventListener
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onCollectNotification(AccountNotificationService.AccountNotificationCollectEvent event){
    // 유저가 북마크한 문제들의 post history를  가져온다.
    List<ProblemPostHistory> results =  problemPostHistoryRepository.findByAccountIdAndLastDatetime(event.getAccountId(), event.getLastTime());

    List<ProblemPostNotification> newNotifications = new ArrayList<>(results.size());
    //가져온 history를 problempostnotification으로 변환하여 생성한다.
    for(ProblemPostHistory history : results){
      ProblemPostNotification newNotification = ProblemPostNotification.builder()
              .account(accountRepository.getReferenceById(event.getAccountId()))
              .post(history.getPost())
              .problem(history.getProblem())
              .timestamp(history.getCreateDate())
              .valid(Boolean.TRUE)
              .build();
      newNotifications.add(newNotification);
    }
    accountNotificationRepository.saveAll(newNotifications); // (리팩토링 필요) IDENTITY 채번 전략으로 인한 부하 존재.
  }

}
