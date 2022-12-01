package com.zerobase.cms.user.domain.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;

}
