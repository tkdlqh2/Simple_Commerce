package com.zerobase.cms.order.client.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChangeBalanceForm {
    private String from;
    private String message;
    private Integer money;

}
