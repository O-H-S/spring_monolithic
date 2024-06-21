package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.problem.service.ProblemPostService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



// 별칭을 id와 매핑하는 서비스. (임시 구현됨, 차후 동적으로 변경 가능하도록 만들기 + 캐시(WAS, redis))
@Service
@RequiredArgsConstructor
public class BoardAliasService {

  final BoardService boardService;
  final Map<String, List<String>> prefixTableByAlias = Map.of(
          "free", List.of("자유"),
          "notice", List.of("공지"),
          "problem", List.of("문제 토론", "문제토론", ProblemPostService.PROBLEM_BOARD_NAME)
  );

  private Map<String, Integer> aliasToBoardId = new HashMap<>();
  @EventListener(ApplicationReadyEvent.class)
  public void init(){
    List<BoardResponse> boards = boardService.getBoardsReadOnly();
    for (BoardResponse board : boards) {
      String title = board.getTitle();
      String alias = findAliasFromName(title);
      if(alias != null)
        aliasToBoardId.put(alias, board.getId());
      else{

      }
    }
  }
  String findAliasFromName(String name){
    for (Map.Entry<String, List<String>> entry : prefixTableByAlias.entrySet()) {
      String alias = entry.getKey();
      List<String> prefixes = entry.getValue();
      for (String prefix : prefixes) {
        if(name.startsWith(prefix)){
          return alias;
        }
      }
    }
    return null;
  }

  public Integer getBoardId(String alias){
    if(!aliasToBoardId.containsKey(alias))
      throw new EntityNotFoundException("no matched board alias");
    return aliasToBoardId.get(alias);
  }

  public Integer tryGetBoardId(String id) {
    try {
      return Integer.parseInt(id);
    } catch (NumberFormatException e) {
      return getBoardId(id);
    }

  }
}
