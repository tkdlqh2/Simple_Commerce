package com.zerobase.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.application.SignInApplication;
import com.zerobase.cms.user.domain.SignInForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SignInController.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc(addFilters = false)
class SignInControllerTest {

    @MockBean
    SignInApplication signInApplication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_EMAIL = "abc@gmail.com";
    private static final String USER_PASSWORD = "1111";

    @Test
    void signInCustomerSuccess() throws Exception {
        //given

        SignInForm form = new SignInForm(USER_EMAIL,USER_PASSWORD);

        given(signInApplication.customerLoginToken(any()))
                .willReturn("tokenString");
        //when
        //then
        mockMvc.perform(post("/signIn/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("tokenString"))
                .andDo(print());
    }

    @Test
    void signInSellerSuccess() throws Exception {
        //given

        SignInForm form = new SignInForm(USER_EMAIL,USER_PASSWORD);

        given(signInApplication.sellerLoginToken(any()))
                .willReturn("tokenString");
        //when
        //then
        mockMvc.perform(post("/signIn/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("tokenString"))
                .andDo(print());
    }
}