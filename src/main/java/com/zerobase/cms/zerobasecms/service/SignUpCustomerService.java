package com.zerobase.cms.zerobasecms.service;

import com.zerobase.cms.zerobasecms.domain.CustomerRepository;
import com.zerobase.cms.zerobasecms.domain.SignUpForm;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
