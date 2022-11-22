package com.zerobase.cms.user.service;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SignUpCustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SignUpCustomerService service;

    @Test
    void singUp(){
        //given
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("010-1111-2222")
                .build();


        given(passwordEncoder.encode(form.getPassword())).willReturn(form.getPassword());
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        //when
        var result = service.signUp(form);

        //then
        verify(customerRepository,times(1)).save(captor.capture());
        assertEquals("name",result.getName());
        assertNotNull(result.getBirth());
        assertEquals("abc@gmail.com",result.getEmail());
        assertEquals("010-1111-2222",result.getPassword());
    }

}
