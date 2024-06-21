package com.ohs.monolithic.board.controller.mvc;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostForm;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// 게시글 작성과 관련된 웹 컨트롤러
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
        model.addAttribute("targetPostId", null);
        model.addAttribute("targetBoardId", boardID);
        return "post_form";
    }

    //legacy
    /*@PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/write/{id}")
    public String showWriteWindow(Model model, @PathVariable("id") Integer boardID,
                                  @Valid PostForm postForm, BindingResult bindingResult, Principal principal) {

        Account userAccount = this.accountService.getAccount(principal.getName());
        *//*BindingResult 매개변수는 항상 @Valid 매개변수 바로 뒤에 위치해야 한다.
        만약 2개의 매개변수의 위치가 정확하지 않다면 @Valid만 적용이 되어 입력값 검증 실패 시 400 오류가 발생한다.*//*

        if (bindingResult.hasErrors()) {
            return "post_form";
        }
        writeService.create(boardID, postForm, userAccount);
        model.addAttribute("board", boardID);
        return String.format("redirect:/board/%d", boardID); // 질문 저장후 질문목록으로 이동
    }
*/
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String postModify(Model model, PostForm postForm, @PathVariable("id") Long id, @AuthenticationPrincipal AppUser user) {
        // 리팩토링 대상, postService에 위임하기.
        Post post = this.readService.getPost(id, true);
        if(!post.getAuthor().getId().equals(user.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        postForm.setSubject(post.getTitle());
        postForm.setContent(post.getContent());

        model.addAttribute("targetPostId", id);
        model.addAttribute("targetBoardId", post.getBoard().getId());
        return "post_form";
    }

    // legacy
    /*@PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid PostForm postForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "post_form";
        }
        Post post = this.readService.getPost(id, true);
        if (!post.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.writeService.modify(post, postForm.getSubject(), postForm.getContent());
        return String.format("redirect:/post/detail/%s", id);
    }*/



}
