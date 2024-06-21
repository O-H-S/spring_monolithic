package com.ohs.monolithic.board.domain.constants;

import lombok.Getter;
@Getter
public enum PostTagType {
  Highlight, // 유저가 지정한 강조 태그
  System, // 시스템에 의해 지정된 태그
  Normal,
  ;
}
