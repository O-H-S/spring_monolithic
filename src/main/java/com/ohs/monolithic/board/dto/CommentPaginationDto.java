package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentPaginationDto {
  Long id;
  String content;
  @Setter
  Long postId;
  Long writerId;
  String writerNickname;
  String writerProfile;
  Long likeCount;

  @Setter
  Boolean liked;

  LocalDateTime createDate;
  LocalDateTime modifyDate;

  public static CommentPaginationDto of(Comment comment){
    return of(comment, false);
  }
  public static CommentPaginationDto of(Comment comment, Boolean liked){
    CommentPaginationDto dto = new CommentPaginationDto();
    dto.id = comment.getId();
    dto.content = comment.getContent();
    dto.postId = comment.getPost().getId();
    dto.writerId = comment.getAuthor().getId();
    dto.writerNickname = comment.getAuthor().getNickname();
    dto.writerProfile = comment.getAuthor().getProfileImage();
    dto.likeCount = comment.getLikeCount();
    dto.liked = liked;
    dto.createDate = comment.getCreateDate();
    dto.modifyDate = comment.getModifyDate();
    return dto;
  }
}
