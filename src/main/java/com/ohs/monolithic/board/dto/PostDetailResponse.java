package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


public class PostDetailResponse {
  public Integer id;
  public Integer boardID;
  public Long authorID;
  public String authorName;
  public String title;
  public String content;
  public LocalDateTime modifyDate;
  public LocalDateTime createDate;
  public Long likeCount;
  public Long viewCount;


  public static PostDetailResponse of(Post targetPost){
    PostDetailResponse newResponse = new PostDetailResponse();
    newResponse.id = targetPost.getId();
    newResponse.boardID = targetPost.getBoard().getId();
    newResponse.authorID = targetPost.getAuthor().getId();
    newResponse.authorName = targetPost.getAuthor().getUsername();
    newResponse.title = targetPost.getTitle();
    newResponse.content = targetPost.getContent();
    newResponse.modifyDate = targetPost.getModifyDate();
    newResponse.createDate = targetPost.getCreateDate();
    newResponse.likeCount = targetPost.getLikeCount();
    newResponse.viewCount = targetPost.getViewCount();
    return newResponse;
  }

}
