package com.ohs.monolithic;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.service.BoardManageService;
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

    final BoardManageService bService;
    final PostReadService pService;

    static class BoardBriefData{
        public String title;
        public List<String> posts;
    }

    @GetMapping("/")
    public String index(Model model) {

        List<Board> boards = bService.getBoards();
        Map<Integer, List<Post>> boardToLatestPosts = new HashMap<>();

        for (Board board : boards) {
            boardToLatestPosts.put(board.getId(),  pService.getRecentPosts(board, 5));
        }

        model.addAttribute("boards", boards);
        model.addAttribute("boardToLatestPosts", boardToLatestPosts);

        /*List<BoardBriefData> data = Arrays.asList()
        List<Board> boards =  bService.getBoards();
        for (Board b : boards) {
            //System.out.println(b.getTitle());
            //bService.get
            List<Post> recentPosts =  pService.getRecentPosts(b, 5);
            //System.out.println(recentPosts);
        }
        model.addAttribute("items", boards);

         */
        return "index";
    }
}
