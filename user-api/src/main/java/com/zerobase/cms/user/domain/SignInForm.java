package com.zerobase.cms.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignInForm {

    private String email;
    private String password;
}
