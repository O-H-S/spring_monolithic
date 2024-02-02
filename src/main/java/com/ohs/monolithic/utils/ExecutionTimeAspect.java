package com.ohs.monolithic.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Aspect
@Component
public class ExecutionTimeAspect {

    private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
    @Around("@annotation(IncludeExecutionTime)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        if (!bean.isThreadCpuTimeSupported()) {
            throw new UnsupportedOperationException("Thread CPU time measurement is not supported.");
        }

        bean.setThreadCpuTimeEnabled(true);

        long startTime = System.currentTimeMillis();
        long startTime_thread = bean.getCurrentThreadCpuTime();
        try {

            return joinPoint.proceed();
        } finally {
            long timeTaken = System.currentTimeMillis() - startTime;
            long timeTaken_thread = (bean.getCurrentThreadCpuTime() - startTime) / 1_000_000;
            // timeTaken_thread : 실제 시간의 차이가 아닌, 쓰레드 기준에서 할당된 시간이다. 스레드가 블록(blocked)되거나 대기(waiting) 상태에 있을 때 포함하지 않음.
            ExecutionTimeTracker.setExecutionTime(timeTaken);
        }
    }
}