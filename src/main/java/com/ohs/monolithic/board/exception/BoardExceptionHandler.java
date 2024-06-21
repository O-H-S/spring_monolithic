package com.ohs.monolithic.board.exception;

import com.ohs.monolithic.board.controller.rest.BoardApiController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
@RestControllerAdvice(basePackageClasses = BoardApiController.class)
public class BoardExceptionHandler {

    /*@ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<?> handleBoardNotFoundException(BoardNotFoundException ex) {
        // BoardNotFoundException 처리 로직
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }*/
    /*@ExceptionHandler(BoardException.class)
    public ResponseEntity<BoardResponse> handleAccessNotFoundException(BoardException e) {
        //System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }*/

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> handleAccessNotFoundException(BoardNotFoundException e) {
        //System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}