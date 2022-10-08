package com.zerobase.cms.zerobasecms.controller;

import com.zerobase.cms.zerobasecms.application.SignUpApplication;
import com.zerobase.cms.zerobasecms.domain.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm form){
        signUpApplication.customerSignUp(form);
        return ResponseEntity.ok("가입 신청이 완료되었습니다.");
    }

    @PutMapping("/verify/customer")
    public ResponseEntity<String> verifyCustomer(String email, String code){
        signUpApplication.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
