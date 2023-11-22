package com.ohs.monolithic.board.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class BulkInsertForm {
    @NotNull
    @Size(min = 1, max = 100)
    final private String title;

    @NotNull
    @Min(1)  // 최소값 설정
    @Max(10000000)  // 최대값 설정
    final private Long count;
}
