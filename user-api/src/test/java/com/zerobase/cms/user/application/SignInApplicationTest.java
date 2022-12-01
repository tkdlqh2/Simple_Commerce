package com.zerobase.cms.user.application;

import com.zerobase.cms.user.domain.SignInForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.cms.user.service.seller.SellerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.zerobase.cms.user.exception.ErrorCode.LOGIN_CHECK_FAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SignInApplicationTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private SellerService sellerService;
    @Mock
    private JwtAuthenticationProvider provider;
    @InjectMocks
    private SignInApplication signInApplication;

    private static final SignInForm FORM = new SignInForm("abc@gmail.com","1111");
    private static final String TOKEN_STRING= "tokenString";


    @Test
    void customerLoginTokenSuccess() {
        //given
        Customer c = Customer.builder()
                .id(1L)
                .email(FORM.getEmail())
                .build();

        given(customerService.findValidCustomer(FORM.getEmail(),FORM.getPassword()))
                .willReturn(Optional.of(c));

        given(provider.createToken(c.getEmail(),c.getId(), UserType.CUSTOMER)).willReturn(TOKEN_STRING);

        //when
        var result = signInApplication.customerLoginToken(FORM);
        //then

        assertEquals(TOKEN_STRING,result);
    }

    @Test
    void customerLoginTokenFail() {
        //given

        given(customerService.findValidCustomer(FORM.getEmail(),FORM.getPassword()))
                .willReturn(Optional.empty());

        //when
        //then
        try{
            signInApplication.customerLoginToken(FORM);
            throw new RuntimeException("오류가 발생하지 않음");
        }catch (Exception e){
            assertEquals(LOGIN_CHECK_FAIL.getDetail(),e.getMessage());
        }
    }

    @Test
    void sellerLoginTokenSuccess() {
        //given
        Seller s = Seller.builder()
                .id(1L)
                .email(FORM.getEmail())
                .build();

        given(sellerService.findValidSeller(FORM.getEmail(),FORM.getPassword()))
                .willReturn(Optional.of(s));

        given(provider.createToken(s.getEmail(),s.getId(), UserType.SELLER)).willReturn(TOKEN_STRING);

        //when
        var result = signInApplication.sellerLoginToken(FORM);
        //then

        assertEquals(TOKEN_STRING,result);
    }

    @Test
    void sellerLoginTokenFail() {
        //given

        given(sellerService.findValidSeller(FORM.getEmail(),FORM.getPassword()))
                .willReturn(Optional.empty());

        //when
        //then
        try{
            signInApplication.sellerLoginToken(FORM);
            throw new RuntimeException("오류가 발생하지 않음");
        }catch (Exception e){
            assertEquals(LOGIN_CHECK_FAIL.getDetail(),e.getMessage());
        }
    }

}