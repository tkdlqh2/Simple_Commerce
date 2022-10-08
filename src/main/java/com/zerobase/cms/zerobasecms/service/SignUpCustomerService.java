package com.zerobase.cms.zerobasecms.service;

import com.zerobase.cms.zerobasecms.domain.CustomerRepository;
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
    public void changeCustomerValidateEmail(Long customerId,String verificationCode){
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                ()-> new CustomException(ErrorCode.UNREGISTERED_USER)
        );

        customer.setVerificationCode(verificationCode);
        customer.setVerifyExpiredAt(LocalDateTime.now());
    }
}
