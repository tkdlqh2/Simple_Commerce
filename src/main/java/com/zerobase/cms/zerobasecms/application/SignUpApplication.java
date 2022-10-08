package com.zerobase.cms.zerobasecms.application;

import com.zerobase.cms.zerobasecms.client.MailgunClient;
import com.zerobase.cms.zerobasecms.client.mailgun.SendMailForm;
import com.zerobase.cms.zerobasecms.domain.SignUpForm;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import com.zerobase.cms.zerobasecms.exception.CustomException;
import com.zerobase.cms.zerobasecms.exception.ErrorCode;
import com.zerobase.cms.zerobasecms.service.SignUpCustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignUpApplication {
    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;

    public void customerVerify(String email,String code){
        signUpCustomerService.verifyEmail(email, code);
    }

    public String customerSignUp(SignUpForm form){
        if(signUpCustomerService.isEmailExist(form.getEmail())){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_USER);
        }else{
            Customer c= signUpCustomerService.signUp(form);
            LocalDateTime now= LocalDateTime.now();

            String code = getRandomCode();
            SendMailForm sendMailForm = SendMailForm.builder()
                    .from("yhj7124@naver.com")
                    .to(form.getEmail())
                    .subject("Verification Email!")
                    .text(getVerificationEmailBody(form.getEmail(), form.getName(), getRandomCode()))
                    .build();

            mailgunClient.sendEmail(sendMailForm);
            signUpCustomerService.changeCustomerValidateEmail(c.getId(),code);
            return "회원 가입에 성공하였습니다.";
        }
    }

    private String getRandomCode(){
        return RandomStringUtils.random(10,true,true);
    }

    private String getVerificationEmailBody(String email,String name, String code){
        StringBuilder builder = new StringBuilder();
        return builder.append("Hello ").append(name).append(("! Please Click Link for verification.\n\n"))
                .append("http://localhost:8080/customer/signup/verify?email=")
                .append(email)
                .append("&code=")
                .append(code).toString();
    }
}
