package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.board.dto.PostDetailResponse;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.dto.AppUser;
import com.ohs.monolithic.user.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

// 게시글 조회 페이지 , 웹 컨트롤러
// 브라우저에서 게시글을 클릭 했을 때 View 컨트롤러.
@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostViewController {

    private final PostReadService readService;
    private final CommentService commentService;



    @GetMapping("/{id}")
    public String getPostDetail(Model model, @PathVariable("id") Long id, @AuthenticationPrincipal AppUser user){

        Account viewer = user != null ? user.getAccount() : null;

        baseModelMapping(model, id, viewer, new CommentForm());
        return "post_detail";
    }

    // legacy, 댓글 관련 요청은 모두 RESTful api로 처리할 예정.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/comments")
    public String writeComment( Model model, @PathVariable("id") Long id,
                               @Valid CommentForm commentForm, BindingResult bindingResult, @AuthenticationPrincipal AppUser user){

        if (bindingResult.hasErrors()) {
            baseModelMapping(model, id, user.getAccount(), commentForm);
            return "post_detail";
        }

        commentService.createByID(id, commentForm.getContent(), user.getAccountId());
        //baseModelMapping(model, id, viewer, new CommentForm());
        return String.format("redirect:/post/%d", id);
    }



    void baseModelMapping(Model model, Long id, Account viewer, CommentForm commentForm)
    {

        PostDetailResponse response = this.readService.readPost(id, viewer != null? viewer.getId() : null);
        List<CommentPaginationDto> comments = this.commentService.getCommentsAsPage(id, viewer);

        model.addAttribute("myAccount", viewer); // 리팩토링 대상
        model.addAttribute("response", response);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", commentForm);
    }

}
