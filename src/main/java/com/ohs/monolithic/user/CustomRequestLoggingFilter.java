package com.ohs.monolithic.user;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomRequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 로그를 출력
        logger.info("Request URI: " + request.getRequestURI());
        logger.info("Request Method: " + request.getMethod());
        //request.
        // 요청 헤더, 바디 등 추가 정보를 로그로 기록할 수 있습니다.

        // 필터 체인을 계속 진행
        filterChain.doFilter(request, response);
    }
}