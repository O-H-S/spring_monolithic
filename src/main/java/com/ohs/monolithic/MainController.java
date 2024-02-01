package com.ohs.monolithic;

import com.ohs.monolithic.board.BoardPaginationType;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostPaginationService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.user.AccountService;
import com.ohs.monolithic.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Controller
public class MainController {

  final BoardService boardService;
  final AccountService accountService;
  final PostPaginationService postPaginationService;

  @GetMapping("/")
  public String index(Principal currentUser, Model model) {

    boolean requiredDesc = false;
    if(currentUser != null) {
      Account visitor = accountService.getAccount(currentUser.getName());
      if(visitor.getRole() == UserRole.ADMIN) requiredDesc = true;
    }

    List<BoardResponse> boards = boardService.getBoardsReadOnly(true, requiredDesc);
    Map<Integer, List<PostPaginationDto>> boardToLatestPosts = new HashMap<>();

    for (BoardResponse board : boards) {
      boardToLatestPosts.put(board.getId(), postPaginationService.getListWithoutOffset(null, board.getId(), 5));
    }

    model.addAttribute("paginationTypes", BoardPaginationType.values());

    model.addAttribute("boards", boards);
    model.addAttribute("boardToLatestPosts", boardToLatestPosts);

    return "index";
  }
}
