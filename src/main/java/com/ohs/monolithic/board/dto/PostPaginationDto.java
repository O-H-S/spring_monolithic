package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PostPaginationDto{

    Long id;
    String title;
    Long userId;
    String userNickname;
    String userProfile;
    LocalDateTime createDate;
    Integer commentCount;
    Long likeCount;
    Long viewCount;

    List<String> systemTags = new ArrayList<>();
    List<String> highlightTags= new ArrayList<>();

    @QueryProjection // new QPostPaginationDto(...)와 같이 사용할 수 있게된다.
    public PostPaginationDto(Long _id, String _title, Long _userId, String _userNickname, String _userProfile,LocalDateTime _createDate
            , Integer _commentCount, Long _likeCount, Long _viewCount){
        id = _id;
        title = _title;
        userId = _userId;
        userProfile = _userProfile;
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
                origin.getAuthor().getProfileImage(),
                origin.getCreateDate(),
                origin.getCommentCount(),
                origin.getLikeCount(),
                origin.getViewCount()
        );
    }

    public void mapTags(List<PostTag> tags){
        for (PostTag tag : tags) {
            if (tag.getType() != null) {
                switch (tag.getType()) {
                    case System -> this.systemTags.add(tag.getTag().getName());
                    case Highlight -> this.highlightTags.add(tag.getTag().getName());
                }
            }
        }
    }


}
