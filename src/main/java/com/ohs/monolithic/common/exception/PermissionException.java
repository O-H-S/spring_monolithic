package com.ohs.monolithic.common.exception;

// 인증은 되었지만, 비즈니스 로직에 의한 권한 예외.
public class PermissionException extends BusinessException {
  public PermissionException(String message){
    super(ErrorCode.HANDLE_ACCESS_DENIED, message);
  }

}
