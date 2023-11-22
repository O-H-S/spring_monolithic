package com.ohs.monolithic.board.dto;

public class BulkInsertResponse {
    private Long curCount;
    private Long maxCount;

    public void update(Long cur, Long max){
        curCount = cur;
        maxCount = max;
    }
}
