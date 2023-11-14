package com.ohs.monolithic.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("@annotation(IncludeExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        //System.out.println("aspect");
        long startTime = System.currentTimeMillis();
        try {
            //System.out.println("aspect-try");
            return joinPoint.proceed();
        } finally {
            ExecutionTimeTracker.setExecutionTime(System.currentTimeMillis() - startTime);
        }
    }
}