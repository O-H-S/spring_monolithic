package com.ohs.monolithic.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ApiAuthenticationFailureHandler implements AuthenticationFailureHandler {
  final ObjectMapper objectMapper;
  private static final Map<Class<? extends AuthenticationException>, String> exceptionMessages =  Map.of(
          BadCredentialsException.class, "정보불일치",
          UsernameNotFoundException.class, "계정없음",
          AccountExpiredException.class, "계정만료",
          CredentialsExpiredException.class, "비밀번호만료",
          DisabledException.class, "계정비활성화",
          LockedException.class, "계정잠김"
  );

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {

    String message = exceptionMessages.getOrDefault(exception.getClass(), "알수없음");

    // 에러 정보를 JSON 형식으로 구성
    Map<String, Object> errorDetails = new HashMap<>();
    errorDetails.put("error", "Unauthorized");
    errorDetails.put("message", message);

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // ObjectMapper를 사용하여 Map을 JSON으로 변환 후 응답 본문에 출력

    String jsonResponse = objectMapper.writeValueAsString(errorDetails);
    response.getWriter().print(jsonResponse);
    log.info("API 세션 인증 실패 : " + message);
  }

}
