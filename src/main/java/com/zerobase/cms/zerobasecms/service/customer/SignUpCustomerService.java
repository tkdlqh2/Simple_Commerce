package com.zerobase.cms.zerobasecms.service.customer;

import com.zerobase.cms.zerobasecms.domain.repository.CustomerRepository;
import com.zerobase.cms.zerobasecms.domain.SignUpForm;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import com.zerobase.cms.zerobasecms.exception.CustomException;
import com.zerobase.cms.zerobasecms.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public Customer signUp(SignUpForm form){
        Customer customer = Customer.from(form);
        customer.setPassword(passwordEncoder.encode(form.getPassword()));
        customerRepository.save(customer);
        return customer;
    }

    public boolean isEmailExist(String email){
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT)).isPresent();
    }

    @Transactional
    public void verifyEmail(String email,String code){
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(()->new CustomException(ErrorCode.UNREGISTERED_USER));

        if(customer.isVerify()){
            throw new CustomException(ErrorCode.ALREADY_VERIFIED);
        }

        if(!customer.getVerificationCode().equals(code)){
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }

        if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            throw new CustomException(ErrorCode.EXPIRED_CODE);
        }
        customer.setVerify(true);
    }

    @Transactional
    public void changeCustomerValidateEmail(Long customerId,String verificationCode){
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                ()-> new CustomException(ErrorCode.UNREGISTERED_USER)
        );

        customer.setVerificationCode(verificationCode);
        customer.setVerifyExpiredAt(LocalDateTime.now());
    }
}
