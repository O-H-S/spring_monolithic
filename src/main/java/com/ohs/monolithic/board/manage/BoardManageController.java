package com.ohs.monolithic.board.manage;


import com.ohs.monolithic.board.Board;
import com.ohs.monolithic.board.Post;
import com.ohs.monolithic.board.PostRepository;
import com.ohs.monolithic.board.read.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class BoardManageController {
    final BoardManageService bService;
    final PostReadService pService;

    //  http://localhost:8080/board/create?title=abc&desc=dc
    @GetMapping("/create")
    @ResponseBody
    public String createBoard(@RequestParam String title, @RequestParam String desc){
        bService.createBoard(title, desc);
        return "created";
    }

    @GetMapping("/{id}")
    public String showBoard(Model model , @PathVariable("id") Integer id, @RequestParam(value="page", defaultValue="0") int page){

        Page<Post> paging = this.pService.getList(page, id);
        Board curBoard = this.bService.getBoard(id);
        for (Post post : paging) {

            //System.out.println(post.getCommentList().get(0));
        }
        // 현재 페이지를 추상화함.
        model.addAttribute("title", curBoard.getTitle());
        model.addAttribute("desc", curBoard.getDescription());
        model.addAttribute("paging", paging);
        model.addAttribute("board", id);

        return "post_list";
    }

    /*@PostMapping("/write/{id}")
    public String writeComment(Model model, @PathVariable("id") Integer id, @RequestParam String content){
        Post post = this.pReadService.getPost(id);
        cService.create(post, content);
        return String.format("redirect:/post/detail/%s", id);

    }*/




}
