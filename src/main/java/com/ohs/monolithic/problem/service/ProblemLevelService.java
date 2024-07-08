package com.ohs.monolithic.problem.service;


import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// 하드코딩된 형태로 플랫폼 별 난이도에 따른 level를 매핑한다. (난이도 정규화가 목적)
// 비즈니스 로직이 확장되어 동적인 레벨링이 필요해질 수 있으므로 서비스로 분리함.
@Service
@RequiredArgsConstructor
public class ProblemLevelService {

  final ProblemRepository problemRepository;

  static Map<String, Map<String, Float>> levelTable = Map.of(
          "baekjoon", Map.ofEntries(
                  Map.entry("Sprout", 0f),
                  Map.entry("Bronze V", 1f), Map.entry("Bronze IV", 1.3f), Map.entry("Bronze III", 1.6f), Map.entry("Bronze II", 1.9f), Map.entry("Bronze I", 2.1f),
                  Map.entry("Silver V", 2.2f), Map.entry("Silver IV", 2.3f), Map.entry("Silver III", 2.6f), Map.entry("Silver II", 2.9f), Map.entry("Silver I", 3f),
                  Map.entry("Gold V", 3.1f), Map.entry("Gold IV", 3.2f), Map.entry("Gold III", 3.3f), Map.entry("Gold II", 3.4f), Map.entry("Gold I", 3.5f),
                  Map.entry("Platinum V", 3.6f), Map.entry("Platinum IV", 3.7f), Map.entry("Platinum III", 3.8f), Map.entry("Platinum II", 3.9f), Map.entry("Platinum I", 4f),
                  Map.entry("Diamond V", 4.1f), Map.entry("Diamond IV", 4.2f), Map.entry("Diamond III", 4.3f), Map.entry("Diamond II", 4.4f), Map.entry("Diamond I", 4.5f),
                  Map.entry("Ruby V", 4.6f), Map.entry("Ruby IV", 4.7f), Map.entry("Ruby III", 4.8f), Map.entry("Ruby II", 4.9f), Map.entry("Ruby I", 5f)
          ),
          "softeer", Map.ofEntries(
                  Map.entry("level-1", 1f),
                  Map.entry("level-2", 2f),
                  Map.entry("level-3", 3f),
                  Map.entry("level-4", 4f),
                  Map.entry("level-5", 5f)
          ),
          "swea", Map.ofEntries(
                  Map.entry("D1", 0f),
                  Map.entry("D2", 1f),
                  Map.entry("D3", 2f),
                  Map.entry("D4", 2.5f),
                  Map.entry("D5", 3.5f),
                  Map.entry("D6", 4f),
                  Map.entry("D7", 4.5f),
                  Map.entry("D8", 5f)
          ),
          "programmers", Map.ofEntries(
                  Map.entry("level-0", 0f),
                  Map.entry("level-1", 1f),
                  Map.entry("level-2", 2f),
                  Map.entry("level-3", 3f),
                  Map.entry("level-4", 4f),
                  Map.entry("level-5", 5f)
          )
  );

  public Float getLevel(String platform, String difficulty){
    Map<String, Float> platformMap = levelTable.get(platform);
    if (platformMap != null && difficulty != null) {
      return platformMap.get(difficulty);
    }
    return null;
  }

  @Transactional
  public void updateLevels(){
    List<Problem> targets = problemRepository.findCandidateForLeveling();
    for(Problem p : targets){
      p.setLevel(getLevel(p.getPlatform(), p.getDifficulty()));
    }
    problemRepository.saveAll(targets);
  }

}
