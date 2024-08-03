package com.ohs.monolithic.board.controller.mvc;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostPaginationService;
import com.ohs.monolithic.common.utils.IncludeExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
    HTML, Template 반환하는 컨트롤러(클라이언트가 브라우저에서 요청하는 경우)
*/
@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class BoardController {
    final BoardService bService;
    final PostPaginationService paginationService;


    // Test 코드 작성됨.
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/create")
    //@ResponseBody
    public String createBoard(@RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String desc){
        bService.createBoard(title, desc);
        return "redirect:/";
    }

    @IncludeExecutionTime
    @GetMapping("/{id}") // 게시판 리스트의 최초 진입
    public String showBoard(Model model , @PathVariable("id") Integer id){

        BoardResponse curBoard = this.bService.getBoardReadOnly(id, null);
        model.addAttribute("title", curBoard.getTitle());
        model.addAttribute("desc", curBoard.getDescription());
        model.addAttribute("board", id);

        Page<PostPaginationDto> paging = this.paginationService.getPostListAsPage( id, 0 ,10);
        if(paging != null){
            model.addAttribute("paging", paging);
            model.addAttribute("paginationFailed", false);
            return "post_list";
        }
        List<PostPaginationDto> scroll = this.paginationService.getPostListAsScroll(id, null, 10);
        if(scroll != null) {
            model.addAttribute("scroll", scroll);
            Long lastId = scroll.isEmpty() ? null : scroll.get(scroll.size() - 1).getId();
            model.addAttribute("scroll_lastId", lastId);
            model.addAttribute("scroll_isFinal", scroll.size() < 10);
            model.addAttribute("paginationFailed", false);
            return "post_list";
        }
        model.addAttribute("paginationFailed", true);
        return "post_list";
    }

    @IncludeExecutionTime
    @GetMapping(value = "/{id}", params = "page")
    public String showBoardAsPage(Model model , @PathVariable("id") Integer id, @RequestParam(value="page", defaultValue="0") int page){
        BoardResponse curBoard = this.bService.getBoardReadOnly(id,null);
        model.addAttribute("title", curBoard.getTitle());
        model.addAttribute("desc", curBoard.getDescription());
        model.addAttribute("board", id);

        Page<PostPaginationDto> paging = this.paginationService.getPostListAsPage( id, page ,10);
        if(paging != null)
        {
            model.addAttribute("paging", paging);
            model.addAttribute("paginationFailed", false);
            return "post_list";
        }

        model.addAttribute("paginationFailed", true);
        return "post_list";
    }

    @IncludeExecutionTime
    @GetMapping(value = "/{id}", params = "lastPostId")
    public String showBoardAsScroll(Model model , @PathVariable("id") Integer id, @RequestParam(value="lastPostId") Long lastId){
        BoardResponse curBoard = this.bService.getBoardReadOnly(id,null);

        model.addAttribute("title", curBoard.getTitle());
        model.addAttribute("desc", curBoard.getDescription());
        model.addAttribute("board", id);

        List<PostPaginationDto> scroll = this.paginationService.getPostListAsScroll(id, lastId, 10);
        if(scroll != null) {
            model.addAttribute("scroll", scroll);
            Long newlastId = scroll.isEmpty() ? null : scroll.get(scroll.size() - 1).getId();
            model.addAttribute("scroll_lastId", newlastId);
            model.addAttribute("paginationFailed", false);
            return "post_list";
        }
        model.addAttribute("paginationFailed", true);
        return "post_list";
    }



    /*@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/title")
    public String titleChange(@AuthenticationPrincipal UserDetails user, @PathVariable("id") Integer id, @RequestParam String boardTitle){

        BoardResponse target = this.bService.getBoardReadOnly(id,null);
        bService.up
        target.setTitle(boardTitle);
        bService.save(target);

        return "redirect:/";
    }*/

}
