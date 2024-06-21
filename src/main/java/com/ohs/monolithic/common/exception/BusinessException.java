package com.ohs.monolithic.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class BusinessException extends RuntimeException {
  final ErrorCode errorCode;

  public BusinessException(ErrorCode code){
    super(code.getMessage());
    errorCode = code;
  }

  public ErrorResponse getErrorResponse(){
    return ErrorResponse.of(this.getErrorCode());
  }

}
