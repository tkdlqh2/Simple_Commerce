package com.zerobase.cms.zerobasecms.service;

import com.zerobase.cms.zerobasecms.domain.repository.CustomerRepository;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Optional<Customer> findByIdAndEmail(Long id,String email){
        return customerRepository.findById(id)
                .stream().filter(customer-> customer.getEmail().equals(email))
                .findFirst();
    }
    public Optional<Customer> findValidCustomer(String emaail,String password){

        return  customerRepository.findByEmail(emaail).stream().filter(
                customer -> customer.getPassword().equals(password) && customer.isVerify()
        ).findFirst();
    }
}
