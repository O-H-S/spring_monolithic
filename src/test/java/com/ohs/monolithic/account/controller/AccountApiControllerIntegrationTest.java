package com.ohs.monolithic.account.controller;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.utils.IntegrationTestBase;
import com.ohs.monolithic.utils.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountApiControllerIntegrationTest  extends IntegrationTestBase {

  /*================================================================================

       계정 정보 (부분)변경

 ================================================================================*/
  @Test
  @DisplayName("PATCH /api/accounts/{id}: 성공 - 200  ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void patchAccount_0() throws Exception {
    //given
    Account me = initSecurityUserAccount();

    // when
    Map<String, String> form = new HashMap<>();
    form.put("nickname", "changedNickname");
    form.put("email", "changedEmail");

    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .patch(
                            String.format("/api/accounts/%d", me.getId() )
                    )
                    .with(csrf())
                    .content(gson.toJson(form))
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isOk());
    result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("accounts/patch/succeeded-recommended",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("PATCH /api/accounts/{id}: 자신의 계정이 아니면 수정 불가 -   ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void patchAccount_1() throws Exception {
    //given
    Account me = initSecurityUserAccount();
    Account other = helper.simpleAccount();
    // when
    Map<String, String> form = new HashMap<>();
    form.put("nickname", "changedNickname");
    form.put("email", "changedEmail");

    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .patch(
                            String.format("/api/accounts/%d", other.getId() )
                    )
                    .with(csrf())
                    .content(gson.toJson(form))
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isForbidden());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("accounts/patch/failed-accessdenied",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("PATCH /api/accounts/{id}: 제약사항 위배시 실패 -   ")
  @WithMockCustomUser(username = "hyeonsu", authorities = "USER")
  public void patchAccount_2() throws Exception {
    //given
    Account me = initSecurityUserAccount();
    Account other = helper.simpleAccount();
    // when
    Map<String, String> form = new HashMap<>();
    form.put("nickname", other.getNickname());
    form.put("email", "changedEmail");

    ResultActions result = mockMvc.perform(
            MockMvcRequestBuilders
                    .patch(
                            String.format("/api/accounts/%d", me.getId() )
                    )
                    .with(csrf())
                    .content(gson.toJson(form))
                    .contentType(MediaType.APPLICATION_JSON)

    ).andDo(print());

    // then
    result.andExpect(status().isBadRequest());
    //result.andExpect(content().contentType(MediaType.APPLICATION_JSON));

    result.andDo(document("accounts/patch/failed-violation",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }





}
