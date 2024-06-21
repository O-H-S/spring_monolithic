package com.ohs.monolithic.board.dto;


import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import com.ohs.monolithic.board.domain.Board;
import lombok.*;

import java.util.Set;


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
    public BoardPaginationType paginationType;

    public Boolean writable; // 인증된 유저가 작성 가능한지 여부
    public Set<String> writableMethods; // 작성 가능 하다면, 어떤 경로를 통해 작성 가능한지를 나타냄. (ex. direct(게시판에서 직접 작성), problem(개별 문제 토론 게시판에서 작성)

    public static BoardResponse fromEntity(Board board, Long postCounts) {
        return fromEntity(board, postCounts, null, null);
    }

    public static BoardResponse fromEntity(Board board, Long postCounts, Boolean writable, Set<String> writableMethods) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .postCounts(postCounts)
                .writable(writable)
                .writableMethods(writableMethods)
                .paginationType(board.getPaginationType())
                .build();
    }
}
