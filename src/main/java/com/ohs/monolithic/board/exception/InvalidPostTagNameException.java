package com.ohs.monolithic.board.exception;

import com.ohs.monolithic.common.exception.BusinessException;
import com.ohs.monolithic.common.exception.ErrorCode;
import com.ohs.monolithic.common.exception.ErrorResponse;

import java.util.List;
import java.util.Map;

public class InvalidPostTagNameException extends BusinessException {
  Map<String, String> invalids;
  public InvalidPostTagNameException(Map<String, String> invalids){
    super(ErrorCode.INVALID_TAG_NAMES);
    this.invalids = invalids;
  }

  @Override
  public ErrorResponse getErrorResponse(){
    ErrorResponse response = super.getErrorResponse();
    response.setData(invalids);
    return response;
  }

}
