package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor
@Setter
@Getter
public class PostDetailResponse extends PostPaginationDto {

  public Integer boardId;


  public String content;

  public LocalDateTime modifyDate;
  public Integer commentCount;
  public Boolean mine;
  public Boolean liked;

  public List<String> normalTags= new ArrayList<>();
  public static PostDetailResponse of(Post targetPost, Boolean mine, Boolean liked, List<PostTag> tags){
    PostDetailResponse newResponse = new PostDetailResponse();
    newResponse.id = targetPost.getId();
    newResponse.boardId = targetPost.getBoard().getId();
    newResponse.userId = targetPost.getAuthor().getId();
    newResponse.userNickname = targetPost.getAuthor().getNickname();
    newResponse.userProfile = targetPost.getAuthor().getProfileImage();
    newResponse.title = targetPost.getTitle();
    newResponse.content = targetPost.getContent();
    newResponse.modifyDate = targetPost.getModifyDate();
    newResponse.createDate = targetPost.getCreateDate();
    newResponse.likeCount = targetPost.getLikeCount();
    newResponse.viewCount = targetPost.getViewCount();
    newResponse.commentCount = targetPost.getCommentCount();
    newResponse.liked = liked;
    newResponse.mine = mine;


    // n+1 발생하는지 테스트 필요
    for (PostTag tag : tags) {
      if (tag.getType() == null) {
        continue;
      } else {
        switch (tag.getType()) {
          case System -> newResponse.systemTags.add(tag.getTag().getName());
          case Highlight -> newResponse.highlightTags.add(tag.getTag().getName());
          case Normal -> newResponse.normalTags.add(tag.getTag().getName());
        }
      }
    }

    return newResponse;
  }

}
