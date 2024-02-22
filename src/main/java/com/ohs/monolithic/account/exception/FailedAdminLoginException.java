package com.ohs.monolithic.account.exception;

/*@ResponseStatus(value = HttpStatus., reason = "i")*/
public class FailedAdminLoginException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public FailedAdminLoginException(String message) {
        super(message);
    }
}