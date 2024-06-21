package com.ohs.monolithic.problem.collect.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProblemDto {
  private String platform;
  private String platformId;
  private String title;
  private String difficulty;
  private String link;
  private LocalDateTime foundDate;
  private Integer collectorVersion;
}
