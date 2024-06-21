package com.ohs.monolithic.account.exception;

import com.ohs.monolithic.common.exception.BusinessException;
import com.ohs.monolithic.common.exception.ErrorCode;
import com.ohs.monolithic.common.exception.ErrorResponse;
import lombok.Getter;

@Getter
public class FailedAccountCreationException extends BusinessException {
  //Map<String, String> targets;
  String detailReason;
  public FailedAccountCreationException(String detailReason){
    super(ErrorCode.INVALID_ACCOUNT_CREATION_FORM );
    this.detailReason = detailReason;
  }

  @Override
  public ErrorResponse getErrorResponse(){
    ErrorResponse response = super.getErrorResponse();
    response.setData(detailReason);
    return response;
  }

}
