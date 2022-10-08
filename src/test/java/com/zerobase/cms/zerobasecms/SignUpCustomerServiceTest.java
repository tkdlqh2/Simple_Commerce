package com.zerobase.cms.zerobasecms;

import com.zerobase.cms.zerobasecms.domain.repository.CustomerRepository;
import com.zerobase.cms.zerobasecms.domain.SignUpForm;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import com.zerobase.cms.zerobasecms.service.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class SignUpCustomerServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    SignUpCustomerService signUpCustomerService;

    @Test
    void signUpTestSuccess(){
        //given
        SignUpForm form = SignUpForm.builder()
                        .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("1111")
                .phone("010-1111-1111")
                .build();

        given(passwordEncoder.encode(anyString())).willReturn("2222");
        //when

        Customer customer = signUpCustomerService.signUp(form);
        //then
        assertEquals("name",customer.getName());
        assertEquals(LocalDate.now(),customer.getBirth());
        assertEquals("abc@gmail.com",customer.getEmail());
        assertEquals("2222",customer.getPassword());
        assertEquals("010-1111-1111",customer.getPhone());

    }
}
