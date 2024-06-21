package com.ohs.monolithic.common.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.PrimitiveIterator;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성자를 protected로 설정
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
  private String message;
  private String code;
  private Object data;

   public static ErrorResponse of(ErrorCode errCode){
    ErrorResponse instance = new ErrorResponse();
    instance.setMessage(errCode.getMessage());
    instance.setCode(errCode.getCode());
    return instance;
  }
 /* public static <T> ErrorResponse<T> of(ErrorCode errCode){
    ErrorResponse<T> instance = new ErrorResponse<T>();
    instance.setMessage(errCode.getMessage());
    instance.setCode(errCode.getCode());
    return instance;
  }*/
}
