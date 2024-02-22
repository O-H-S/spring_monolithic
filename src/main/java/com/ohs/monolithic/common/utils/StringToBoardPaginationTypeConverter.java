package com.ohs.monolithic.common.utils;

import com.ohs.monolithic.board.BoardPaginationType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToBoardPaginationTypeConverter implements Converter<String, BoardPaginationType> {
  @Override
  public BoardPaginationType convert(String source) {
    if (source.isEmpty()) {
      return null;
    }
    try {
      return BoardPaginationType.valueOf(source);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}