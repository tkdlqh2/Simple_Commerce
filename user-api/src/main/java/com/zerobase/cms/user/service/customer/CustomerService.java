package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Customer> findByIdAndEmail(Long id, String email){
        return customerRepository.findById(id)
                .stream().filter(customer-> customer.getEmail().equals(email))
                .findFirst();
    }
    public Optional<Customer> findValidCustomer(String email,String password){

        return  customerRepository.findByEmail(email).stream().filter(
                customer -> passwordEncoder.matches(password, customer.getPassword()) && customer.isVerify()
        ).findFirst();
    }
}
