package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostView;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.repository.PostViewRepository;
import com.ohs.monolithic.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// 조회수 서비스
@Service
@RequiredArgsConstructor
public class PostViewService {
  final PostRepository postRepository;
  final PostViewRepository postViewRepository;
  final AccountRepository accountRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void view(Post post, Long memberId){

    PostView view = postViewRepository.findByPostIdAndUserId(post.getId(), memberId);
    if(view == null) {
      view = PostView.builder()
              .post(post)
              .member(accountRepository.getReferenceById(memberId))
              .build();

      postRepository.updateViewCount(post.getId(), 1);
      postViewRepository.save(view);
      return;
    }
    view.incrementCount();
    postViewRepository.save(view);
  }


}
