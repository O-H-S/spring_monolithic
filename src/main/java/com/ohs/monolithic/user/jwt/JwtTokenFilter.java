package com.ohs.monolithic.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;

//@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("jwt doFilter");
        // 1. 쿠기에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            System.out.println("jwt validate");
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {

        //LogoutFilter
        // 헤더를 이용한 방법 : String bearerToken = request.getHeader("Authorization");
        // 요청에서 "jwt-token" 쿠키를 찾습니다.
        Cookie cookie = WebUtils.getCookie(request, "jwt-token");
        // 해당 쿠키가 존재하면 그 값을 반환합니다.
        if (cookie != null) {
            System.out.println("find jwt cookie");
            String bearerToken = cookie.getValue();
            System.out.println(bearerToken);
            if (StringUtils.hasText(bearerToken)/* && bearerToken.startsWith("Bearer")*/) {
                return bearerToken;
            }
        }
        return null;
    }


}