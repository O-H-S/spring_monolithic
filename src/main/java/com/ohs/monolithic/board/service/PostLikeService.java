package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostLike;
import com.ohs.monolithic.board.repository.PostLikeRepository;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.user.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// 게시글 추천 기능
@Service
@RequiredArgsConstructor
public class PostLikeService {

  final private PostLikeRepository postLikeRepository;
  final private PostRepository postRepository;

  @PersistenceContext
  EntityManager em;

  // Test Exists(Itegration), 동시성 테스트 필요

  @Transactional(readOnly = true)
  public Boolean doesLikePost(Long postId, Long memberId){

    return postLikeRepository.existsByPostAndUserAndValidIsTrue(
            em.getReference(Post.class, postId),
            em.getReference(Account.class, memberId)
    );
  }


  @Transactional
  public Boolean likePost(Long postId, Account member) {
    //commentLikeRepository.findB
    Optional<PostLike> postLikeOp = postLikeRepository.findPostLikeWithLock(postId, member);
    if (postLikeOp.isPresent()) {
      PostLike postLike = postLikeOp.get();
      if (!postLike.getValid()) {
        postLike.setValid(Boolean.TRUE);
        postLikeRepository.save(postLike);
        postRepository.addLikeCount(postId, 1L); // likeCount를 변경하는 트랜잭션은 오직 여기 뿐이므로, 위의 PostLike 쓰기락으로 동시 진입을 방지함. (즉, 여기서의 Update에서 읽는 postLike값은 고립수준과 상관없이 항상 최신의 데이터이다.)
        // 하지만, 다른 트랜잭션이 업데이트 전의 likeCount를 덮어씌우는 상황은 조심해야함. (save(post)와 같은 코드 찾아보기)
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    PostLike newPostLike = PostLike.builder()
            .post(em.getReference(Post.class, postId))
            .member(member)
            .valid(Boolean.TRUE)
            .build();

    postLikeRepository.save(newPostLike);
    postRepository.addLikeCount(postId, 1L);
    return Boolean.TRUE;
  }

  @Transactional
  public Pair<Boolean, Long> likePostEx(Long postId, Account member){
    Boolean result = this.likePost(postId, member);
    Optional<Post> post = this.postRepository.findById(postId);
    return Pair.of(result, post.get().getLikeCount());
  }



  @Transactional
  public Boolean unlikePost(Long postId, Account member) {
    //commentLikeRepository.findB
    Optional<PostLike> postLikeOp = postLikeRepository.findPostLikeWithLock(postId, member);
    if (postLikeOp.isPresent()) {
      PostLike postLike = postLikeOp.get();
      if (postLike.getValid()) {
        postLike.setValid(Boolean.FALSE);
        postLikeRepository.save(postLike);
        postRepository.addLikeCount(postId, -1L);
        return Boolean.TRUE;
      }
    }
    return Boolean.FALSE;
  }

  @Transactional
  public Pair<Boolean, Long> unlikePostEx(Long postId, Account member){
    Boolean result = this.unlikePost(postId, member);
    Optional<Post> post = this.postRepository.findById(postId);
    return Pair.of(result, post.get().getLikeCount());
  }


}
