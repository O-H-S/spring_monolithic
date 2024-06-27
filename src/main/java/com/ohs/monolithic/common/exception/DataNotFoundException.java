package com.ohs.monolithic.common.exception;

import com.ohs.monolithic.common.exception.BusinessException;
import com.ohs.monolithic.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends BusinessException {
    String dataType;
    public DataNotFoundException(String dataType){
        super(ErrorCode.ENTITY_NOT_FOUND);
        this.dataType = dataType;
    }
    public DataNotFoundException(String dataType, String reason){
        super(ErrorCode.ENTITY_NOT_FOUND, reason);
        this.dataType = dataType;
    }
    @Override
    public ErrorResponse getErrorResponse(){
        ErrorResponse response = super.getErrorResponse();
        response.setData(dataType);
        return response;
    }

}