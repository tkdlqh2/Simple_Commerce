package com.zerobase.cms.order.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCode errorCode;
    private final int status;
    private static final ObjectMapper mapper = new ObjectMapper();

    public CustomException(ErrorCode errorCode){
        super(errorCode.getDetail());
        this.errorCode = errorCode;
        this.status = errorCode.getHttpStatus().value();
    }
}
