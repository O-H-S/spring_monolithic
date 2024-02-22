package com.ohs.monolithic.common.utils;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@DependsOn("springSecurityFilterChain")
@RequiredArgsConstructor
public class SecurityFilterListPrinter {


    final private FilterChainProxy filterChainProxy;


    @PostConstruct
    public void printSecurityFilters() {
        System.out.println("[필터 체인 List]");
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        // 필터 체인은 여러개 존재 가능.
        for (SecurityFilterChain filterChain : filterChains) {
            // 하나의 체인에는 여러개의 필터가 존재.
            System.out.println("<< " + filterChain.getClass().getName() + " >>");
            for (Filter filter : filterChain.getFilters()) {
                System.out.println("--" + filter.getClass().getName());
            }
        }
    }
}