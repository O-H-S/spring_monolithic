package com.ohs.monolithic.auth.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohs.monolithic.account.dto.AccountResponse;
import com.ohs.monolithic.auth.domain.AppUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
  final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {



    // 에러 정보를 JSON 형식으로 구성
    Map<String, Object> content = new HashMap<>();

    Object principal = authentication.getPrincipal();

    log.info("API 세션 인증 성공 : " + principal.toString());
    Object loggedAccountData = request.getAttribute("loggedAccountData");

    if (principal instanceof AppUser appUser && loggedAccountData != null) {
      content.put("data", loggedAccountData);
    }

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_CREATED);

    // ObjectMapper를 사용하여 Map을 JSON으로 변환 후 응답 본문에 출력

    String jsonResponse = objectMapper.writeValueAsString(content);
    response.getWriter().print(jsonResponse);
  }
}
