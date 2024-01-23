package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostViewService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

// 게시글 조회 페이지 , 웹 컨트롤러
// 브라우저에서 게시글을 클릭 했을 때 View 컨트롤러.
@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostReadController {

    private final PostReadService readService;
    private final CommentService commentService;
    private final AccountService accountService;



    @GetMapping("/{id}")
    public String getPostDetail(Model model, @PathVariable("id") Integer id, Principal currentUser){

        Account viewer = currentUser != null ? accountService.getAccount(currentUser.getName()) : null;

        baseModelMapping(model, id, viewer, new CommentForm());
        return "post_detail";
    }

    // legacy, 댓글 관련 요청은 모두 RESTful api로 처리할 예정.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/comments")
    public String writeComment( Model model, @PathVariable("id") Integer id,
                               @Valid CommentForm commentForm, BindingResult bindingResult, Principal currentUser){
        Account viewer =  accountService.getAccount(currentUser.getName());

        if (bindingResult.hasErrors()) {
            baseModelMapping(model, id, viewer, commentForm);
            return "post_detail";
        }

        commentService.createByID(id, commentForm.getContent(), viewer.getId());
        baseModelMapping(model, id, viewer, new CommentForm());
        return "post_detail";
    }



    void baseModelMapping(Model model, Integer id, Account viewer, CommentForm commentForm)
    {

        PostDetailResponse response = this.readService.readPost(id, viewer);
        List<CommentPaginationDto> comments = this.commentService.getCommentsAsPage(id, viewer);


        model.addAttribute("response", response);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", commentForm);
    }

}
