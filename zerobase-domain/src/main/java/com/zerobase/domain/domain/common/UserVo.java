package com.zerobase.domain.domain.common;

import lombok.Getter;

@Getter
public class UserVo{

    private Long id;
    private String email;

    public UserVo(Long id, String email){
        this.id = id;
        this.email = email;
    }
}
