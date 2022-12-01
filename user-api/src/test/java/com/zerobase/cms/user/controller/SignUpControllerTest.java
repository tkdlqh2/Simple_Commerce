package com.zerobase.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.application.SignUpApplication;
import com.zerobase.cms.user.domain.SignUpForm;
import com.zerobase.cms.user.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static com.zerobase.cms.user.exception.ErrorCode.ALREADY_REGISTERED_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SignUpController.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc(addFilters = false)
class SignUpControllerTest {

    @MockBean
    private SignUpApplication signUpApplication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_EMAIL = "abc@gmail.com";
    private static final String CODE = "code";

    @Test
    void customerSignUpSuccess() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();

        given(signUpApplication.customerSignUp(any()))
                .willReturn("");
        //when
        //then
        mockMvc.perform(post("/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("가입 신청이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    void customerSignUpFail_ArgumentException() throws Exception {
        //given

        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd")
                .birth(LocalDate.of(1990,1,1))
                .build();
        //when
        //then
        mockMvc.perform(post("/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(print());
    }

    @Test
    void customerSignUpFail_AlreadyRegisteredUser() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();

        doThrow(new CustomException(ALREADY_REGISTERED_USER))
                .when(signUpApplication).customerSignUp(any());
        //when
        //then
        mockMvc.perform(post("/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_USER.getDetail()))
                .andDo(print());
    }

    @Test
    void customerVerifySuccess() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();
        doNothing().when(signUpApplication).customerVerify(USER_EMAIL,CODE);
        //when
        //then
        mockMvc.perform(get("/signup/customer/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("인증이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    void sellerSignUpSuccess() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();

        given(signUpApplication.sellerSignUp(any()))
                .willReturn("");
        //when
        //then
        mockMvc.perform(post("/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("가입 신청이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    void sellerSignUpFail_ArgumentException() throws Exception {
        //given

        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd")
                .birth(LocalDate.of(1990,1,1))
                .build();
        //when
        //then
        mockMvc.perform(post("/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andDo(print());
    }

    @Test
    void sellerSignUpFail_AlreadyRegisteredUser() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();

        doThrow(new CustomException(ALREADY_REGISTERED_USER))
                .when(signUpApplication).sellerSignUp(any());
        //when
        //then
        mockMvc.perform(post("/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ALREADY_REGISTERED_USER.getDetail()))
                .andDo(print());
    }

    @Test
    void sellerVerifySuccess() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abc@gmail.com")
                .name("홍길동")
                .password("abcd2@ef")
                .birth(LocalDate.of(1990,1,1))
                .build();
        doNothing().when(signUpApplication).sellerVerify(USER_EMAIL,CODE);
        //when
        //then
        mockMvc.perform(get("/signup/seller/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("인증이 완료되었습니다."))
                .andDo(print());
    }

}