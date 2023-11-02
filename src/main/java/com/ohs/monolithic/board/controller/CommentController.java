package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.board.domain.Comment;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
    final PostReadService pReadService;
    final PostWriteService pWriteService;
    final CommentService cService;
    final AccountService accountService;

    //현재 로그인한 사용자에 대한 정보를 알기 위해서는 스프링 시큐리티가 제공하는 Principal 객체를 사용해야 한다.
    //Principal 객체는 로그인을 해야만 생성되는 객체
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write/{id}")
    public String writeComment(HttpServletRequest request, Model model, @PathVariable("id") Integer id,
                               @Valid CommentForm commentForm, BindingResult bindingResult, Principal principal){
        Post post = this.pReadService.getPost(id);
        //request.
        Account userAccount = this.accountService.getAccount(principal.getName());
        //model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "myForm", bindingResult);
        if (bindingResult.hasErrors()) {

            model.addAttribute("post", post);
            model.addAttribute("commentList", cService.getComments(id));
            return "post_detail";
            //model.addAttribute("test", 1232);
            //request.setAttribute("test", 1111);
            //return String.format("forward:/post/detail/%s", id);
            /*forward를 사용하여 같은 서블릿 컨테이너 내에서 요청을 다른 컨트롤러로 전달할 수 있습니다. 이 방식은 클라이언트가 알지 못하고 서버 내부에서 처리됩니다.*/
        }

        cService.create(post, commentForm.getContent(), userAccount);
        return String.format("redirect:/post/detail/%s", id);
        /*HTTP 리다이렉트(302 상태 코드)가 발생합니다. 이는 클라이언트(대개 웹 브라우저)가 새로운 URL로 요청을 다시 보내게 하는 것을 의미합니다. 즉, 이 경우 서버가 클라이언트에게 새로운 URL로 요청을 다시 보내라는 지시를 내리며, 클라이언트가 그 지시를 따라 새로운 요청을 서버에 보냅니다.

                간단히 말해서, 이 과정은 클라이언트를 거쳐 다시 서버로 요청이 이루어지게 됩니다. 이는 내부적으로 서버가 무언가를 처리하는 것과는 다르며, 네트워크 라운드트립이 발생합니다. 따라서 이 방식은 상대적으로 느리며, 브라우저의 주소창에도 새로운 URL이 표시됩니다.*/

    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        Comment answer = this.cService.getComment(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "comment_form";
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentrModify(@Valid CommentForm answerForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment answer = this.cService.getComment(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.cService.modify(answer, answerForm.getContent());
        return String.format("redirect:/post/detail/%s", answer.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal, @PathVariable("id") Integer id) {
        Comment answer = this.cService.getComment(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.cService.delete(answer);
        return String.format("redirect:/post/detail/%s", answer.getPost().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.cService.getComment(id);
        Account siteUser = this.accountService.getAccount(principal.getName());
        this.cService.vote(comment, siteUser);
        return String.format("redirect:/post/detail/%s", comment.getPost().getId());
    }

}
