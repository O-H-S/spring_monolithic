package com.ohs.monolithic.problem.domain;

import lombok.Getter;

@Getter
public enum ProblemBookmarkType {
  Unsolved, // 아직 풀지 않음
  FailedCompletely, // 접근 방식도 모름
  TooLong, // 오래 걸림
  Success, // 시간내에 품
  SuccessDifferently, // 다른 방법으로도 풀 수 있음.
  ;
}
