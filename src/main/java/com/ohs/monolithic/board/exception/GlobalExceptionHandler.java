package com.ohs.monolithic.board.exception;

import com.ohs.monolithic.board.dto.BoardResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        //System.out.println(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }






    @ExceptionHandler(MissingServletRequestParameterException.class)// 400 Bad Request 상태 코드 반환
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException e, HttpServletRequest request) {
        // 현재 요청의 정보 얻기
        String requestURL = request.getRequestURL().toString();
        String method = request.getMethod();

        // 에러 메시지 생성
        String errorMessage = String.format("Error in request %s %s: %s", method, requestURL, e.getMessage());

        // 적절한 에러 처리 로직
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)// 400 Bad Request 상태 코드 반환
    public ResponseEntity<String> handleIncorrectParams(IllegalArgumentException e, HttpServletRequest request) {
        // 현재 요청의 정보 얻기
        String requestURL = request.getRequestURL().toString();
        String method = request.getMethod();

        // 에러 메시지 생성
        String errorMessage = String.format("Error in request %s %s: %s", method, requestURL, e.getMessage());

        // 적절한 에러 처리 로직
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    /*@ExceptionHandler(CsrfException.class)
    //@ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden 상태 코드 반환
    public ResponseEntity<String>  handleCsrfException(CsrfException e) {
        // CSRF 예외 처리 로직
        // 예를 들어, 로그 기록, 사용자에게 에러 메시지 반환 등
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(CsrfException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) // 403 Forbidden 반환
    public String handleCsrfException2(CsrfException ex) {
        // CSRF 예외 처리 로직
        return "csrfErrorPage"; // 예를 들어, CSRF 에러 페이지 반환
    }*/
}