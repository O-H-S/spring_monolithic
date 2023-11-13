package com.ohs.monolithic.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostPaginationDto{

    Integer id;
    String title;
    Long userId;
    String userName;
    LocalDateTime createDate;
    Integer commentCount;

    @QueryProjection
    public PostPaginationDto(Integer _id, String _title, Long _userId, String _userName, LocalDateTime _createDate, Integer _commentCount){
        id = _id;
        title = _title;
        userId = _userId;
        userName = _userName;
        createDate = _createDate;
        commentCount = _commentCount;
    }
}
