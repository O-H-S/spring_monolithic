package com.ohs.monolithic.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationHeaderFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
            && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));

    // 인증 상태 헤더 설정
    response.setHeader("X-Authenticated", String.valueOf(isAuthenticated));

    filterChain.doFilter(request, response);

  }
}
