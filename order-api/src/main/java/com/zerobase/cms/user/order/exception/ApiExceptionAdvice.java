package com.zerobase.cms.user.order.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomException.CustomeExceptionResponse> exceptionHandler(HttpServletRequest request,
                                                                                     final CustomException e) {

        return ResponseEntity
                .status(e.getStatus())
                .body(CustomException.CustomeExceptionResponse.builder()
                        .message(e.getMessage())
                        .code(e.getErrorCode().name())
                        .status(e.getStatus())
                        .build());
    }

}
