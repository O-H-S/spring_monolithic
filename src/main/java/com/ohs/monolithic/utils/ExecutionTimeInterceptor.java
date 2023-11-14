package com.ohs.monolithic.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ExecutionTimeInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long etime = ExecutionTimeTracker.getExecutionTime();
        if (etime != null) {
            modelAndView.addObject("executionTime", etime);
            ExecutionTimeTracker.clear();
        }
    }
}