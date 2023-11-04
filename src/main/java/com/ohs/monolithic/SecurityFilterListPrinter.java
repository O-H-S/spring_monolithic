package com.ohs.monolithic;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("springSecurityFilterChain")
@RequiredArgsConstructor
public class SecurityFilterListPrinter {


    final private FilterChainProxy filterChainProxy;

    @PostConstruct
    public void printSecurityFilters() {
        System.out.println("[필터 체인 정보]");
        //System.out.println(org.springframework.security.core.SpringSecurityCoreVersion.getVersion());

        //UsernamePasswordAuthenticationFilter
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        // 필터 체인은 여러개 존재 가능.
        for (SecurityFilterChain filterChain : filterChains) {
            // 하나의 체인에는 여러개의 필터가 존재.
            System.out.println("-" + filterChain.getClass().getName());
            for (Filter filter : filterChain.getFilters()) {
                System.out.println("--" + filter.getClass().getName());
            }
        }
    }
}