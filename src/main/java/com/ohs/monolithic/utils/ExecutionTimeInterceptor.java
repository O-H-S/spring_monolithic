package com.ohs.monolithic.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ExecutionTimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        System.out.println( request.getRequestURL());

        //OpenEntityManagerInViewInterceptor
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long etime = ExecutionTimeTracker.getExecutionTime();
        System.out.println( request.getRequestURL() + " end");
        if (etime != null) {

            modelAndView.addObject("executionTime", etime);
            ExecutionTimeTracker.clear();
        }
    }
}