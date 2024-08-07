package com.ohs.monolithic.common.controller;

import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.board.service.BoardService;
import com.ohs.monolithic.board.service.PostPaginationService;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
  public String index(@AuthenticationPrincipal AppUser user, Model model) {

    boolean requiredDesc = false;
    if(user != null) {
      if(user.isAdmin()) requiredDesc = true;
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
  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("OK");
  }
}
