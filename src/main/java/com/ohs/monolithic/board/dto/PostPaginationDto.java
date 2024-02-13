package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.Post;
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
    String userNickname;
    LocalDateTime createDate;
    Integer commentCount;
    Long likeCount;
    Long viewCount;
    @QueryProjection
    public PostPaginationDto(Long _id, String _title, Long _userId, String _userNickname, LocalDateTime _createDate
            , Integer _commentCount, Long _likeCount, Long _viewCount){
        id = _id;
        title = _title;
        userId = _userId;
        userNickname = _userNickname;
        createDate = _createDate;
        commentCount = _commentCount;
        likeCount = _likeCount;
        viewCount = _viewCount;
    }

    public static PostPaginationDto of(Post origin){
        return new PostPaginationDto(
                origin.getId(),
                origin.getTitle(),
                origin.getAuthor().getId(),
                origin.getAuthor().getNickname(),
                origin.getCreateDate(),
                origin.getCommentCount(),
                origin.getLikeCount(),
                origin.getViewCount()
        );
    }
}
