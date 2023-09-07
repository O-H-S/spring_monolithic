package com.ohs.monolithic.board.write;


import com.ohs.monolithic.board.Post;
import com.ohs.monolithic.board.PostForm;
import com.ohs.monolithic.board.read.PostReadService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
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


@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostWriteController {

    private final PostWriteService writeService;
    private final PostReadService readService;
    private final AccountService accountService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/write/{id}")
    public String showWriteWindow(Model model, PostForm postForm, @PathVariable("id") Integer boardID){
        model.addAttribute("board", boardID);
        return "post_form";
    }

    // @PreAuthorize("isAuthenticated()") 애너테이션이 붙은 메서드는 로그인이 필요한 메서드를 의미한다.
    // 만약 @PreAuthorize("isAuthenticated()") 애너테이션이 적용된 메서드가 로그아웃 상태에서 호출되면 로그인 페이지로 이동된다.
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/write/{id}")
    public String showWriteWindow(Model model, @PathVariable("id") Integer boardID,
                                  @Valid PostForm postForm, BindingResult bindingResult, Principal principal) {

        Account userAccount = this.accountService.getAccount(principal.getName());
        /*BindingResult 매개변수는 항상 @Valid 매개변수 바로 뒤에 위치해야 한다.
        만약 2개의 매개변수의 위치가 정확하지 않다면 @Valid만 적용이 되어 입력값 검증 실패 시 400 오류가 발생한다.*/

        if (bindingResult.hasErrors()) {
            return "post_form";
        }
        writeService.create(boardID, postForm.getSubject(), postForm.getContent(), userAccount);
        model.addAttribute("board", boardID);
        return String.format("redirect:/board/%d", boardID); // 질문 저장후 질문목록으로 이동
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(PostForm postForm, @PathVariable("id") Integer id, Principal principal) {
        Post post = this.readService.getPost(id);
        if(!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postForm.setSubject(post.getTitle());
        postForm.setContent(post.getContent());
        return "post_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid PostForm postForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post_form";
        }
        Post post = this.readService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.writeService.modify(post, postForm.getSubject(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.readService.getPost(id);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.writeService.delete(post);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String postVote(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.readService.getPost(id);
        Account siteUser = this.accountService.getAccount(principal.getName());
        this.writeService.vote(post, siteUser);
        return String.format("redirect:/post/detail/%s", id);
    }

    /*@GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
        Post post = this.readService.getPost(id);
        model.addAttribute("post", post);
        return "post_detail";
    }
*/

}
