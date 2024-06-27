package com.ohs.monolithic.board.exception;

import com.ohs.monolithic.board.controller.rest.BoardApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 특정 컨트롤러에 대해서만 exception handler를 정의할 수 있다.
@Deprecated
@RestControllerAdvice(basePackageClasses = BoardApiController.class)
public class BoardExceptionHandler {


}