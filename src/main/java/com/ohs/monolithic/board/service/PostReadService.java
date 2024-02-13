package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.user.domain.Account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository repository;
    private final PostViewService postViewService;
    private final PostLikeService postLikeService;



    @Transactional(readOnly = true)
    public PostDetailResponse readPost(Long postID, Account viewer){
        Post targetPost = getPost(postID, true);
        Boolean isLiked = Boolean.FALSE;
        Boolean isMine = Boolean.FALSE;
        if(viewer != null) {
            // 조회수 카운팅 트랜잭션이 실패하더라도 게시글을 반환한다.
            try {
                postViewService.view(targetPost, viewer); // 독립된 트랜잭션
            } catch (Exception e) {
                System.out.println("Error updating post view count. : " + e.toString());
            }

            isLiked = postLikeService.doesLikePost(postID, viewer.getId());
            isMine = viewer.getId().equals( targetPost.getAuthor().getId());
        }

        return PostDetailResponse.of(targetPost, isMine, isLiked);
    }

    public Post getPost(Long id){
        return this.getPost(id, false);
    }

    public Post getPost(Long id, Boolean relatedData ) {

        Optional<Post> question = null;
        if (relatedData)
            question = this.repository.findWithAuthorAndBoard(id);
        else{
            question = this.repository.findById(id);
        }
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }


   @Transactional(readOnly = true)
    public Long calculateCount(Integer boardId) {
        return repository.countByBoardId(boardId);
    }

}





