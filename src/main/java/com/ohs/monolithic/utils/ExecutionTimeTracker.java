package com.ohs.monolithic.utils;

public class ExecutionTimeTracker {
    private static final ThreadLocal<Long> executionTime = new ThreadLocal<>();

    public static void setExecutionTime(long time) {
        executionTime.set(time);
    }

    public static Long getExecutionTime() {
        return executionTime.get();
    }

    public static void clear() {
        executionTime.remove();
    }
}