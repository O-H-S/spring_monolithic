package com.ohs.monolithic.problem.dto;


import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.ProblemBookmarkType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ProblemPaginationDto {
  Long id;
  String platform;
  String title;
  String difficulty;
  Float level;
  String link;
  LocalDateTime foundDate;
  Integer postCount;
  ProblemBookmarkType bookmarkType;

  @QueryProjection
  public ProblemPaginationDto(Long id,
                              String platform,
                              String title,
                              String difficulty,
                              Float level,
                              String link,
                              Integer postCount,
                              LocalDateTime foundDate,
                              ProblemBookmarkType bookmarkType){
    this.id= id;
    this.platform= platform;
    this.title = title;
    this.difficulty = difficulty;
    this.link = link;
    this.level = level;
    this.postCount = postCount;
    this.foundDate = foundDate;
    this.bookmarkType = bookmarkType;

  }

  public static ProblemPaginationDto of(Problem origin, ProblemBookmarkType bookmarkType){
    return new ProblemPaginationDto(
      origin.getId(),
      origin.getPlatform(),
      origin.getTitle(),
      origin.getDifficulty(),
      origin.getLevel(),
      origin.getLink(),
      origin.getPostCount(),
      origin.getFoundDate(),
      bookmarkType
      //origin.getFoundDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    );
  }


}
