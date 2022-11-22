package com.zerobase.cms.user.client.mailgun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}
