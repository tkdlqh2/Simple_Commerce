package com.zerobase.cms.user.service.seller;

import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.domain.model.Seller;
import com.zerobase.cms.user.domain.repository.SellerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.cms.user.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpSellerServiceTest {

    @Mock
    SellerRepository sellerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SignUpSellerService signUpSellerService;

    @Test
    void singUpSuccess(){
        //given
        SignUpForm form = SignUpForm.builder()
                .name("name")
                .birth(LocalDate.now())
                .email("abc@gmail.com")
                .password("010-1111-2222")
                .build();


        given(passwordEncoder.encode(form.getPassword())).willReturn(form.getPassword());
        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);

        //when
        var result = signUpSellerService.signUp(form);

        //then
        verify(sellerRepository,times(1)).save(captor.capture());
        assertEquals("name",result.getName());
        assertNotNull(result.getBirth());
        assertEquals("abc@gmail.com",result.getEmail());
        assertEquals("010-1111-2222",result.getPassword());
    }

    @Test
    void verifyEmailSuccess() {
        //given
        Seller seller = Seller.builder()
                .verify(false)
                .verificationCode("code")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        given(sellerRepository.findByEmail(anyString())).willReturn(Optional.of(seller));
        //when
        signUpSellerService.verifyEmail("","code");
//        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        //then
//        verify(customerRepository,times(1)).save(captor);
//        var customerCaptured = captor.getValue();
//        assertEquals(true,customerCaptured);
    }

    @Test
    void verifyEmailFail_NoUser() {
        //given
        given(sellerRepository.findByEmail(anyString())).willReturn(Optional.empty());
        //when
        //then
        try{
            signUpSellerService.verifyEmail("","code");

            throw new RuntimeException("예외가 일어나지 않음");
        }catch (Exception e){
            assertEquals(UNREGISTERED_USER.getDetail(),e.getMessage());
        }
    }

    @Test
    void verifyEmailFail_AlreadyVerified() {
        //given
        Seller seller = Seller.builder()
                .verify(true)
                .verificationCode("code")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        given(sellerRepository.findByEmail(anyString())).willReturn(Optional.of(seller));
        //when
        //then
        try{
            signUpSellerService.verifyEmail("","code");

            throw new RuntimeException("예외가 일어나지 않음");
        }catch (Exception e){
            assertEquals(ALREADY_VERIFIED.getDetail(),e.getMessage());
        }
    }

    @Test
    void verifyEmailFail_wrongCode() {
        //given
        Seller seller = Seller.builder()
                .verify(false)
                .verificationCode("code")
                .verifyExpiredAt(LocalDateTime.now().plusDays(1))
                .build();

        given(sellerRepository.findByEmail(anyString())).willReturn(Optional.of(seller));
        //when
        //then
        try{
            signUpSellerService.verifyEmail("","code2");

            throw new RuntimeException("예외가 일어나지 않음");
        }catch (Exception e){
            assertEquals(WRONG_VERIFICATION.getDetail(),e.getMessage());
        }
    }

    @Test
    void verifyEmailFail_expired() {
        //given
        Seller seller = Seller.builder()
                .verify(false)
                .verificationCode("code")
                .verifyExpiredAt(LocalDateTime.now().minusDays(1))
                .build();

        given(sellerRepository.findByEmail(anyString())).willReturn(Optional.of(seller));
        //when
        //then
        try{
            signUpSellerService.verifyEmail("","code");

            throw new RuntimeException("예외가 일어나지 않음");
        }catch (Exception e){
            assertEquals(EXPIRED_CODE.getDetail(),e.getMessage());
        }
    }

    @Test
    void changeSellerValidateEmailSuccess() {
        //given
        Seller seller = Seller.builder()
                .verify(false)
                .verificationCode("code")
                .verifyExpiredAt(LocalDateTime.now().minusDays(1))
                .build();

        given(sellerRepository.findById(1L)).willReturn(Optional.of(seller));
        //when
        signUpSellerService.changeSellerValidateEmail(1L,"code");
        //then
    }

    @Test
    void changeSellerValidateEmailFail_NoUser() {
        //given
        given(sellerRepository.findById(1L)).willReturn(Optional.empty());

        //when
        try{
            signUpSellerService.changeSellerValidateEmail(1L,"code");

            throw new RuntimeException("예외가 일어나지 않음");
        }catch (Exception e){
            assertEquals(UNREGISTERED_USER.getDetail(),e.getMessage());
        }
        //then
    }

}