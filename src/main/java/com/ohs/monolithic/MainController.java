package com.ohs.monolithic;

import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Controller
public class MainController {

  final BoardService bService;
  final PostReadService pService;

  @GetMapping("/")
  public String index(Model model) {

    List<BoardResponse> boards = bService.getBoardsReadOnly(true, false);
    Map<Integer, List<PostPaginationDto>> boardToLatestPosts = new HashMap<>();

    for (BoardResponse board : boards) {
      boardToLatestPosts.put(board.getId(), pService.getListWithoutOffset(null, board.getId(), 5));
    }

    model.addAttribute("boards", boards);
    model.addAttribute("boardToLatestPosts", boardToLatestPosts);

    return "index";
  }
}
