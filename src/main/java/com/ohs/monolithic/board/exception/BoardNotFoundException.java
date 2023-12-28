package com.ohs.monolithic.board.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardNotFoundException extends RuntimeException {

    public Integer id;
    public BoardNotFoundException(Integer id, String message) {
        super(message);
        this.id = id;
    }
    /*private final BoardErrorResult errorResult;*/

}

/*
@Getter
@RequiredArgsConstructor
public enum BoardErrorResult {

    DUPLICATED_MEMBERSHIP_REGISTER(HttpStatus.BAD_REQUEST, "Duplicated Membership Register Request"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}*/
