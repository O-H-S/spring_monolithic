package com.ohs.monolithic.board.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
@RestControllerAdvice
public class BoardExceptionHandler {

    /*@ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<?> handleBoardNotFoundException(BoardNotFoundException ex) {
        // BoardNotFoundException 처리 로직
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }*/


}