package com.ohs.monolithic.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // Common
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", " Invalid Input Value"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", " Invalid Input Value"),
  REQUEST_REQUIRED_BODY(HttpStatus.BAD_REQUEST, "C003", " Required request body is missing"),
  HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access is Denied"),
  ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "Entity Not Found"),

  //DATA_INTEGRITY_VIOLATION(HttpStatus.BAD_REQUEST, "C050", "DB : 제약조건 위반"),

  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C100", "알 수 없는 오류가 발생하였습니다."),
  NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "C101", "아직 구현되지 않은 요청입니다"),


  // Board
  NO_BOARD_PERMISSION(HttpStatus.FORBIDDEN, "B006", "게시판 이용 권한이 없습니다"),
  INVALID_TAG_NAMES(HttpStatus.BAD_REQUEST, "B102", "올바르지 않은 태그명 입니다."),

  // Account
  INVALID_ACCOUNT_CREATION_FORM(HttpStatus.BAD_REQUEST, "A001", "올바르지 않은 계정 생성 입력입니다."),


  ;
  private final String code;
  private final String message;
  private final HttpStatus status;

  ErrorCode(final HttpStatus status, final String code, final String message) {
    this.status = status;
    this.message = message;
    this.code = code;
  }
}
