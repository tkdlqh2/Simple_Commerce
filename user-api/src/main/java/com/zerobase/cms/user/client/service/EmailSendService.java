package com.zerobase.cms.user.client.service;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.client.mailgun.SendMailForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSendService {
    private final MailgunClient mailgunClient;

    public void sendEmail(){
        SendMailForm form = SendMailForm.builder()
                .from("zerobase-test")
                .to("yhj7124@naver.com")
                .subject("hello")
                .text("text")
                .build();

        mailgunClient.sendEmail(form);
    }
}
