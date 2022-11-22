package com.zerobase.cms.user.controller;

import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody @Valid SignUpForm form){
        signUpApplication.customerSignUp(form);
        return ResponseEntity.ok("가입 신청이 완료되었습니다.");
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        signUpApplication.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody  @Valid SignUpForm form){
        signUpApplication.sellerSignUp(form);
        return ResponseEntity.ok("가입 신청이 완료되었습니다.");
    }

    @GetMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(String type,String email, String code){
        signUpApplication.sellerVerify(email,code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
