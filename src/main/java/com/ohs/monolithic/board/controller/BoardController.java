package com.ohs.monolithic.board.controller;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.utils.IncludeExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/*
    HTML, Template 반환하는 컨트롤러(클라이언트가 브라우저에서 요청하는 경우)
*/
@RequiredArgsConstructor
@RequestMapping("/board")
@Controller
public class BoardController {
    final BoardService bService;
    final PostReadService pService;


    // Test 코드 작성됨.
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/create")
    //@ResponseBody
    public String createBoard(@RequestParam(defaultValue = "") String title, @RequestParam(defaultValue = "") String desc){
        bService.createBoard(title, desc);
        return "redirect:/";
    }

    @IncludeExecutionTime
    @GetMapping("/{id}")
    public String showBoard(Model model , @PathVariable("id") Integer id, @RequestParam(value="page", defaultValue="0") int page){

        Page<PostPaginationDto> paging = this.pService.getListWithCovering(page,id);

        Board curBoard = this.bService.getBoard(id);

        model.addAttribute("title", curBoard.getTitle());
        model.addAttribute("desc", curBoard.getDescription());
        model.addAttribute("paging", paging);
        model.addAttribute("board", id);

        return "post_list";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/title")
    public String titleChange(@AuthenticationPrincipal UserDetails user, @PathVariable("id") Integer id, @RequestParam String boardTitle){

        Board target = bService.getBoard(id);
        target.setTitle(boardTitle);
        bService.save(target);

        return "redirect:/";
    }

}
