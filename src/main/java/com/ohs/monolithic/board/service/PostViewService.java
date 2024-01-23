package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostView;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.repository.PostViewRepository;
import com.ohs.monolithic.user.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostViewService {
  final PostRepository postRepository;
  final PostViewRepository postViewRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void view(Post post, Account member){

    PostView view = postViewRepository.findByPostIdAndUserId(post.getId(), member.getId());
    if(view == null) {
      view = PostView.builder()
              .post(post)
              .member(member)
              .build();

      postRepository.updateViewCount(post.getId(), 1);
      postViewRepository.save(view);
      return;
    }
    view.incrementCount();
    postViewRepository.save(view);
  }


}
