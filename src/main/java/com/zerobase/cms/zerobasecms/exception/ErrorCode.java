package com.zerobase.cms.zerobasecms.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNREGISTERED_USER(HttpStatus.BAD_REQUEST,"회원 정보를 찾을 수 없습니다."),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST,"이미 가입된 회원입니다.");

    private final HttpStatus httpStatus;
    private final String detail;
}
