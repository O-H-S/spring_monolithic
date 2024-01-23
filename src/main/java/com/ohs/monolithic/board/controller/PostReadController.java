package com.ohs.monolithic.board.controller;


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
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

// 게시글 조회 페이지 , 웹 컨트롤러
// 브라우저에서 게시글을 클릭 했을 때 View에 대한 컨트롤러.
@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostReadController {

    private final PostReadService readService;
    private final PostViewService postViewService;  // legacy
    private final CommentService commentService;
    private final AccountService accountService;

    // legacy 예정,  댓글 작성은 브라우저가 별도의 api 컨트롤러로 호출할 것.
    @PostMapping(value = "/detail/{id}")
    public String detailFromComment(@PathVariable("id") Integer id, CommentForm commentForm, Model model, HttpServletRequest request, @AuthenticationPrincipal UserDetails currentUser) {

        return detail(model, id, commentForm, currentUser);
    }



    // legacy
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, CommentForm commentForm, @AuthenticationPrincipal UserDetails currentUser) {
        Post post = this.readService.getPost(id, true);

        model.addAttribute("boardID", post.getBoard().getId());
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentService.getCommentsReadOnly(id));

        if(currentUser != null)
        {
            Account member = accountService.getAccount(currentUser.getUsername());
            postViewService.view(post, member);
        }

        return "post_detail_legacy";
    }


    @GetMapping("/{id}")
    public String getPostDetail(Model model, @PathVariable("id") Integer id, Principal currentUser){
        Account viewer = currentUser != null ? accountService.getAccount(currentUser.getName()) : null;

        PostDetailResponse response = this.readService.readPost(id, viewer);
        List<CommentPaginationDto> comments = this.commentService.getCommentsAsPage(id, viewer);

        model.addAttribute("response", response);
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", new CommentForm());
        return "post_detail";
    }

    /*@PostMapping("/{id}/comments")
    public String writeComment()*/

}
