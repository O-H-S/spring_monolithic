package com.ohs.monolithic.board.dto;


import com.ohs.monolithic.board.domain.Board;
import lombok.*;


// record 개념 정리 후, 리팩토링하기.
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // querydsl에서 매핑할때 기본 생성자 필요함.
public class BoardResponse {
    public Integer id;
    public String title;
    public String description;
    public Long postCounts;

    public static BoardResponse fromEntity(Board board, Long postCounts) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .postCounts(postCounts)
                .build();
    }
}
