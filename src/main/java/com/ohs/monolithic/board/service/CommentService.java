package com.ohs.monolithic.board.service;


import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.*;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.board.repository.CommentRepository;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.account.domain.Account;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CommentService {
    final CommentRepository cRepo;
    final PostRepository pRepo;
    final AccountRepository accountRepository;
    @PersistenceContext
    EntityManager em;


    @Transactional(readOnly = true)
    public List<CommentPaginationDto> getCommentsAsPage(Long postID, AppUser viewer){
        Post targetPost = em.getReference(Post.class, postID);
        return cRepo.getCommentsByPost(targetPost, accountRepository.getReferenceById(viewer.getAccountId()));
    }

    @Transactional(readOnly = true)
    public CommentPaginationDto getCommentBy(Long commentId, Long accountId){
        return cRepo.getCommentById(commentId, accountId);
    }

    @Transactional(readOnly = true)
    public CommentPaginationResponse getComments(Long postID, AppUser user){
        Post targetPost = em.getReference(Post.class, postID);
        List<CommentPaginationDto> comments = cRepo.getCommentsByPost(targetPost, user  != null ?accountRepository.getReferenceById(user.getAccountId()):null);
        CommentPaginationResponse response = new CommentPaginationResponse();

        response.setData(comments);
        response.setTotalCounts(0L);
        response.setTotalPages(0L);

        return response;
    }

    @Transactional
    public Comment create(Post post, String content, Account account) {
        pRepo.updateCommentCount(post.getId(), 1);

        Comment newComment = Comment.builder()
                .author(account)
                .post(post)
                .content(content)
                .build();
        cRepo.save(newComment);

        return newComment;
    }

    @Transactional
    public CommentCreationResponse createByID(Long postId, String content, Long accountId) {
        Post post = em.getReference(Post.class, postId);
        Account account = em.getReference(Account.class, accountId);
        pRepo.updateCommentCount(postId, 1);

        Comment newComment = Comment.builder()
                .author(account)
                .post(post)
                .content(content)
                .build();
        cRepo.save(newComment);

        CommentCreationResponse response = new CommentCreationResponse();
        response.setCommentCount((long)post.getCommentCount());
        response.setCommentData(CommentPaginationDto.of(newComment));

        return response;
    }




    @Transactional
    public CommentDeleteResponse deleteCommentBy(Long commentId, AppUser user)  {

        Comment targetComment = cRepo.getComment(commentId);
        if(targetComment == null){
            throw new DataNotFoundException("존재하지 않는 댓글입니다.");
        }

        if (!targetComment.getAuthor().getId().equals(user.getAccountId())) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다");
        }

        Post post = targetComment.getPost();
        targetComment.setDeleted(Boolean.TRUE);
        cRepo.save(targetComment);
        pRepo.updateCommentCount(post.getId(), -1);

        CommentDeleteResponse response = new CommentDeleteResponse();
        response.setPostId(post.getId());
        response.setCommentCount(post.getCommentCount());
        return response;
    }

    @Transactional
    public void modifyCommentBy(Long commentId, AppUser user, CommentForm form)  {

        Comment targetComment = cRepo.getComment(commentId);
        if(targetComment == null){
            throw new DataNotFoundException("존재하지 않는 댓글입니다.");
        }

        if (!targetComment.getAuthor().getId().equals(user.getAccountId())) {
            throw new AccessDeniedException("댓글 수정 권한이 없습니다");
        }

        targetComment.setContent(form.getContent());
        targetComment.setModifyDate(LocalDateTime.now());
        cRepo.save(targetComment);
    }





    public Comment getComment(Long id) {
        Optional<Comment> answer = this.cRepo.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("entity(comment) not found");
        }
    }






}
