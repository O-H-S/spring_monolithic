package com.ohs.monolithic.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum BoardPaginationType {
  Offset,
  Offset_CountCache,
  Offset_CountCache_CoveringIndex,
  Cursor,
  Hybrid,
  ;
}
