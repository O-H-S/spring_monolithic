package com.ohs.monolithic.board.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


// record 개념 정리 후, 리팩토링하기.
@Getter
@Builder
@RequiredArgsConstructor
public class BoardResponse {
    public final Integer id;
    public final String title;
    public final String description;
}
