package com.ohs.monolithic.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostPaginationDto{

    Long id;
    String title;
    Long userId;
    String userName;
    LocalDateTime createDate;
    Integer commentCount;
    Long likeCount;
    Long viewCount;
    @QueryProjection
    public PostPaginationDto(Long _id, String _title, Long _userId, String _userName, LocalDateTime _createDate
            , Integer _commentCount, Long _likeCount, Long _viewCount){
        id = _id;
        title = _title;
        userId = _userId;
        userName = _userName;
        createDate = _createDate;
        commentCount = _commentCount;
        likeCount = _likeCount;
        viewCount = _viewCount;
    }
}
