package com.zerobase.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.CustomerBalanceHistory;
import com.zerobase.cms.user.exception.CustomException;
import com.zerobase.cms.user.service.customer.CustomerBalanceService;
import com.zerobase.cms.user.service.customer.CustomerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.zerobase.cms.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.zerobase.cms.user.exception.ErrorCode.UNREGISTERED_USER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @MockBean
    private JwtAuthenticationProvider provider;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerBalanceService customerBalanceService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "abc@gmail.com";

    @BeforeEach
    void setup(){
        UserVo userVo = new UserVo(USER_ID,USER_EMAIL);
        given(provider.getUser(anyString())).willReturn(userVo);
    }


    @Test
    void createAccountSuccess() throws Exception {
        //given


        Customer customer = Customer.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .build();
        given(customerService.findByIdAndEmail(USER_ID,USER_EMAIL))
                .willReturn(Optional.of(customer));
        //when
        //then
        mockMvc.perform(get("/customer/getInfo")
                        .header("X-AUTH-TOKEN",""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.balance").value(0))
                .andDo(print());
    }

    @Test
    void createAccountFail() throws Exception {
        //given

        given(customerService.findByIdAndEmail(USER_ID,USER_EMAIL))
                .willReturn(Optional.empty());
        //when
        //then
        mockMvc.perform(get("/customer/getInfo")
                        .header("X-AUTH-TOKEN",""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UNREGISTERED_USER.getDetail()))
                .andDo(print());
    }

    @Test
    void changeBalanceSuccess() throws Exception {
        //given

        ChangeBalanceForm form = new ChangeBalanceForm("from","message",20000);
        given(customerBalanceService.changeBalance(eq(USER_ID),any()))
                .willReturn(CustomerBalanceHistory.builder()
                        .currentMoney(25000)
                        .build());
        //when
        //then
        mockMvc.perform(post("/customer/balance")
                        .header("X-AUTH-TOKEN","")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(25000))
                .andDo(print());
    }

    @Test
    void changeBalanceFail_NoCustomer() throws Exception {
        //given

        ChangeBalanceForm form = new ChangeBalanceForm("from","message",20000);
        doThrow(new CustomException(UNREGISTERED_USER))
                .when(customerBalanceService).changeBalance(eq(USER_ID),any());

        //when
        //then
        mockMvc.perform(post("/customer/balance")
                        .header("X-AUTH-TOKEN","")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(UNREGISTERED_USER.getDetail()))
                .andDo(print());
    }

    @Test
    void changeBalanceFail_NotEnoughBalance() throws Exception {
        //given

        ChangeBalanceForm form = new ChangeBalanceForm("from","message",20000);
        doThrow(new CustomException(NOT_ENOUGH_BALANCE))
                .when(customerBalanceService).changeBalance(eq(USER_ID),any());

        //when
        //then
        mockMvc.perform(post("/customer/balance")
                        .header("X-AUTH-TOKEN","")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(NOT_ENOUGH_BALANCE.getDetail()))
                .andDo(print());
    }
}