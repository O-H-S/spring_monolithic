package com.ohs.monolithic.problem.service;

import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.IntegrationTestWithMySQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Consumer;

public class ProblemLevelServiceIntegrationTest extends IntegrationTestWithMySQL {

  @Autowired
  ProblemLevelService problemLevelService;

  @Autowired
  ProblemRepository problemRepository;

  @Test
  @DisplayName("레벨 업데이트")
  public void updateLevels(){
    Consumer<Problem> c = (p) -> {p.setLevel(null);};
    helper.simpleProblem("programmers", "프로그래머스 일반문제", "level-2", 0f, c,3);
    helper.simpleProblem("baekjoon", "백준 연습문제", "Silver III",0f,c,3);
    helper.simpleProblem("softeer", "소프티어 연습문제", "level-5",0f,c,3);
    helper.simpleProblem("swea", "소프티어 연습문제", "D3",0f, c,3);
    helper.simpleProblem("swea", "소프티어 연습문제2", null,0f, c,3);
    helper.simpleProblem("softeer", "소프티어 연습문제", "level-515",0f,c,3);
    helper.simpleProblem("testPlaform", "테스트 연습문제", "hard",0f,c,3);

    problemLevelService.updateLevels();

    List<Problem> cand =  problemRepository.findCandidateForLeveling();
    //


  }
}
