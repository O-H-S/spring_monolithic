package com.ohs.monolithic.auth.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

// 권한이 없는 경로에 접근했을 때의 처리
// API 필터체인에서 사용되고, 에러 응답 메세지를 보내준다.
// 필터에서 모두 열어주고, 컨트롤러에서 권한 체크를 하기 때문에 임의로 작성함.
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
          throws IOException, ServletException {

    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "abc");
  }
}