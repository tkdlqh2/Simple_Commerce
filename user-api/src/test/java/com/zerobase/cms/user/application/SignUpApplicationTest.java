package com.zerobase.cms.user.application;

import com.zerobase.cms.user.client.MailgunClient;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.service.customer.SignUpCustomerService;
import com.zerobase.cms.user.service.seller.SignUpSellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static com.zerobase.cms.user.exception.ErrorCode.ALREADY_REGISTERED_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class SignUpApplicationTest {

    @Mock
    private MailgunClient mailgunClient;

    @Mock
    private SignUpCustomerService signUpCustomerService;

    @Mock
    private SignUpSellerService signUpSellerService;

    @InjectMocks
    private SignUpApplication signUpApplication;

    private static final String EMAIL = "abc@gmail.com";
    private static final String NAME = "홍길동";
    private static final String PASSWORD = "1111";
    private static final String PASSWORD_ENCODED = "2222";
    private static final LocalDate BIRTH = LocalDate.now().minusYears(1);
    private static final String PHONE = "010-1111-2222";

    @Test
    void customerSignUpSuccess() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .birth(BIRTH)
                .phone(PHONE)
                .build();

        Customer c = Customer.builder()
                .id(1L)
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD_ENCODED)
                .birth(BIRTH)
                .phone(PHONE).build();

        given(signUpCustomerService.isEmailExist(EMAIL)).willReturn(false);
        given(signUpCustomerService.signUp(form)).willReturn(c);
        given(mailgunClient.sendEmail(any())).willReturn(null);
        doNothing().when(signUpCustomerService).changeCustomerValidateEmail(eq(1L),any());

        //when
        var result = signUpApplication.customerSignUp(form);
        //then
        assertEquals("회원 가입에 성공하였습니다.",result);
    }

    @Test
    void customerSignUpFail_AlreadyRegistered() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .birth(BIRTH)
                .phone(PHONE)
                .build();

        given(signUpCustomerService.isEmailExist(EMAIL)).willReturn(true);

        //when
        //then

        try{
            signUpApplication.customerSignUp(form);
            throw new RuntimeException("에러가 발생하지 않음");
        }catch (Exception e){
            assertEquals(ALREADY_REGISTERED_USER.getDetail(),e.getMessage());
        }
    }

    @Test
    void sellerSignUpSuccess() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .birth(BIRTH)
                .phone(PHONE)
                .build();

        Seller s = Seller.builder()
                .id(1L)
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD_ENCODED)
                .birth(BIRTH)
                .phone(PHONE).build();

        given(signUpSellerService.isEmailExist(EMAIL)).willReturn(false);
        given(signUpSellerService.signUp(form)).willReturn(s);
        given(mailgunClient.sendEmail(any())).willReturn(null);
        doNothing().when(signUpSellerService).changeSellerValidateEmail(eq(1L),any());

        //when
        var result = signUpApplication.sellerSignUp(form);
        //then
        assertEquals("회원 가입에 성공하였습니다.",result);
    }

    @Test
    void sellerSignUpFail_AlreadyRegistered() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .birth(BIRTH)
                .phone(PHONE)
                .build();

        given(signUpSellerService.isEmailExist(EMAIL)).willReturn(true);

        //when
        //then
        try{
            signUpApplication.sellerSignUp(form);
            throw new RuntimeException("에러가 발생하지 않음");
        }catch (Exception e){
            assertEquals(ALREADY_REGISTERED_USER.getDetail(),e.getMessage());
        }
    }
}