package com.ohs.monolithic.problem.collect.dto;

import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class CollectProgressResponse {
  String platform;
  Integer collectorVersion;
  Integer lastWindow;
  LocalDateTime startDate;
  public static CollectProgressResponse of(CollectProgress source){
    CollectProgressResponse response = new CollectProgressResponse();
    response.setPlatform(source.getPlatform());
    response.setCollectorVersion(source.getCollectorVersion());
    response.setLastWindow(source.getLastWindow());
    response.setStartDate(source.getStartDate());
    return response;
  }
}
