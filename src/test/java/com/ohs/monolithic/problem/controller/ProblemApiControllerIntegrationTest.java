package com.ohs.monolithic.problem.controller;

import com.ohs.monolithic.utils.IntegrationTestWithH2;
import com.ohs.monolithic.utils.IntegrationTestWithMySQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProblemApiControllerIntegrationTest extends IntegrationTestWithMySQL {

  @Test
  @DisplayName("GET /api/problems: 전체 문제 조회 성공- 200  ")
  @WithAnonymousUser
  public void getProblemList_0() throws Exception{
    // given
      // 문제 생성
    helper.simpleProblem("baekjoon", "백준 일반문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 일반문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 일반문제 제목", 3);
    helper.simpleProblem("baekjoon", "백준 연습문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 연습문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 연습문제 제목", 3);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/api/problems/") // 끝에 '/'를 붙혀야함
                    .param("page", "0")
                    .param("pageSize", "5")
                    .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

  }

  @Test
  @DisplayName("GET /api/problems: (플랫폼 필터 적용) 조회 성공- 200  ")
  @WithAnonymousUser
  public void getProblemList_1() throws Exception{
    // given
    // 문제 생성
    helper.simpleProblem("baekjoon", "백준 일반문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 일반문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 일반문제 제목", 3);
    helper.simpleProblem("baekjoon", "백준 연습문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 연습문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 연습문제 제목", 3);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/api/problems/") // 끝에 '/'를 붙혀야함
                    .param("page", "0")
                    .param("pageSize", "10")
                    .param("platforms", "baekjoon,softeer")
                    .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

  }


  @Test
  @DisplayName("GET /api/problems: (필터 모두 적용) 조회 성공- 200  ")
  @WithAnonymousUser
  public void getProblemList_2() throws Exception{
    // given
    // 문제 생성
    helper.simpleProblem("baekjoon", "백준 일반문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 일반문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 일반문제 제목", 3);
    helper.simpleProblem("baekjoon", "백준 연습문제 제목", 3);
    helper.simpleProblem("softeer", "소프티어 연습문제 제목", 3);
    helper.simpleProblem("programmers", "프로그래머스 연습문제 제목", 3);

    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/api/problems/") // 끝에 '/'를 붙혀야함
                    .param("page", "0")
                    .param("pageSize", "20")
                    .param("platforms", "baekjoon,softeer")
                    .param("sort", "Level")
                    .param("isDescending", "false")
                    .param("keywords", "일반")
                    .param("minLevel", "1")
                    //.param("minLevel", "1")
                    .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

  }

  @Test
  @DisplayName("GET /api/problems: 유효하지 않는 param - 400  ")
  @WithAnonymousUser
  public void getProblemList_failed_0() throws Exception{
    // given


    // when
    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/api/problems/") // 끝에 '/'를 붙혀야함
                    .param("page", "-1")
                    .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));


    result = mockMvc.perform(
            MockMvcRequestBuilders
                    .get("/api/problems/") // 끝에 '/'를 붙혀야함
                    .param("pageSize", "1000000")
                    .contentType(MediaType.APPLICATION_JSON)
    ).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));


  }

}
