package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.dto.CommentForm;
import com.ohs.monolithic.board.service.CommentService;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.service.PostReadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequiredArgsConstructor
@RequestMapping("/post")
@Controller
public class PostReadController {

    private final PostReadService readService;
    private final CommentService commentService;

    @PostMapping(value = "/detail/{id}")
    public String detailFromComment(@PathVariable("id") Integer id, CommentForm commentForm, Model model, HttpServletRequest request) {
        System.out.println(model.getAttribute("test"));
        System.out.println(request.getAttribute("test"));
        return detail(model, id, commentForm);
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, CommentForm commentForm) {
        Post post = this.readService.getPost(id, true);
/*        Integer cSize = post.getCommentList().size();
        Hibernate.initialize(post.getCommentList());
        System.out.println(post.getId());

        System.out.println( commentService.getComments(id));
        System.out.println(post.getCommentList());*/
        //System.out.println(model.getAttribute("test"));

        //System.out.println(Thread.currentThread().getId());
        /*try {
            // 스레드를 5초(5000 밀리초) 동안 일시 중지
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // 다른 스레드에 의해 중단될 경우 처리
            System.out.println(Thread.currentThread().getId());
            Thread.currentThread().interrupt();
        }*/
        model.addAttribute("boardID", post.getBoard().getId());
        model.addAttribute("post", post);
        model.addAttribute("commentList", commentService.getComments(id));

        return "post_detail";
    }


}
