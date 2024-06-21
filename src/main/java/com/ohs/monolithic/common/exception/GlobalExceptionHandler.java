package com.ohs.monolithic.common.exception;

import com.ohs.monolithic.account.exception.FailedAccountCreationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        //System.out.println(e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

       Throwable cause = e.getCause();
       if(cause instanceof ConstraintViolationException detailException){
           if(detailException.getSQLException().getSQLState().equals( "23505") || detailException.getSQLException().getSQLState().equals( "23000"))
               // 23505는 H2에서 발생
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 값 입니다");
       }

       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }*/

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> detail = new HashMap<>();
        BindingResult result = ex.getBindingResult();

        result.getFieldErrors().forEach(fieldError -> {
            detail.put(fieldError.getField(), fieldError.getDefaultMessage());
            //response.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ErrorCode code = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse response = ErrorResponse.of(code);
        response.setData(detail);
        return new ResponseEntity<>(response, code.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {

        ErrorResponse response = ErrorResponse.of(ErrorCode.ENTITY_NOT_FOUND);
        response.setData(ex.getMessage());
        return new ResponseEntity<>(response, ErrorCode.ENTITY_NOT_FOUND.getStatus());
    }
    // 상위 예외를 아래에 둔다. (순서에 영향을 받음)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex){
        ErrorResponse response = ex.getErrorResponse();
        return new ResponseEntity<>(response, ex.getErrorCode().getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Non-business exception occurred: {} - {}", ex.getClass().getName(), ex.getLocalizedMessage(), ex);


        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_ERROR);
        Map<String, Object> content = new HashMap<>();
        content.put("errorType", ex.getClass());
        content.put("errorMessage", ex.getLocalizedMessage());
        response.setData(content);
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_ERROR.getStatus());
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