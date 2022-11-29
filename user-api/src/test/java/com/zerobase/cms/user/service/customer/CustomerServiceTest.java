package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    CustomerService customerService;

    @Test
    void findByIdAndEmailSuccess() {
        //given
        Customer customer = Customer.builder()
                .id(1L)
                .email("abc@gmail.com").build();

        given(customerRepository.findById(1L))
                .willReturn(Optional.of(customer));
        //when
        var result = customerService.findByIdAndEmail(1L,"abc@gmail.com");
        //then
        assertEquals(customer, result.get());
    }

    @Test
    void findValidCustomerTest() {
        //given
        Customer customer = Customer.builder()
                .email("abc@gmail.com")
                .password("1234")
                .verify(true)
                .build();

        given(customerRepository.findByEmail("abc@gmail.com"))
                .willReturn(Optional.of(customer));
        given(passwordEncoder.matches("1111","1234")).willReturn(true);
        //when
        var result = customerService.findValidCustomer("abc@gmail.com","1111");
        //then
        assertEquals(customer,result.get());
    }


}