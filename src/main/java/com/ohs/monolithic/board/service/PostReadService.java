package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.common.exception.DataNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository repository;
    private final PostViewService postViewService; // 조회수 관련 서비스
    private final PostLikeService postLikeService; // 추천 관련 서비스
    private final PostTagService postTagService;



    @Transactional(readOnly = true)
    public PostDetailResponse readPost(Long postID, Long viewerId){
        if(viewerId != null) {
            try {
                postViewService.view(repository.getReferenceById(postID), viewerId); // 독립된 트랜잭션
            } catch (Exception e) {
                System.out.println("Error updating post view count. : " + e.toString());
            }
        }
        return getPostReadOnly(postID, viewerId, true, true);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostReadOnly(Long postId, Long viewerId, boolean determineLiked, boolean determineMine){
        Post targetPost = getPost(postId, false);
        Boolean isLiked = null;
        Boolean isMine = null;
        if(viewerId != null) {
            if(determineLiked)
                isLiked = postLikeService.doesLikePost(postId, viewerId);
            if(determineMine)
                isMine = viewerId.equals( targetPost.getAuthor().getId());
        }
        List<PostTag> tags = postTagService.getPostTags(postId);


        return PostDetailResponse.of(targetPost, isMine, isLiked, tags);


    }

    // 리팩토링 : private으로 변경할 예정
    public Post getPost(Long id){
        return this.getPost(id, false);
    }

    // 리팩토링 : private으로 변경할 예정
    public Post getPost(Long id, Boolean relatedData ) {

        Optional<Post> question = relatedData ? this.repository.findWithAuthorAndBoard(id) : this.repository.findById(id);

        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("post", "게시글이 존재하지 않습니다");
        }
    }


   @Transactional(readOnly = true)
    public Long calculateCount(Integer boardId) {
        return repository.countByBoardId(boardId);
    }

}





