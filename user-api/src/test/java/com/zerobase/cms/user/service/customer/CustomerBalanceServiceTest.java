package com.zerobase.cms.user.service.customer;

import com.zerobase.cms.user.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.user.domain.model.Customer;
import com.zerobase.cms.user.domain.model.CustomerBalanceHistory;
import com.zerobase.cms.user.domain.repository.CustomerBalanceHistoryRepository;
import com.zerobase.cms.user.domain.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.zerobase.cms.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.zerobase.cms.user.exception.ErrorCode.UNREGISTERED_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerBalanceServiceTest {

    @Mock
    CustomerBalanceHistoryRepository customerBalanceHistoryRepository;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerBalanceService customerBalanceService;

    @Test
    void changeBalanceSuccess_haveLastHistory() {
        //given
        Customer customer = Customer.builder()
                        .id(1L)
                        .build();

        CustomerBalanceHistory history = CustomerBalanceHistory.builder()
                .customer(customer)
                .currentMoney(30000)
                .build();

        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customer.getId()))
                .willReturn(Optional.of(history));

        ArgumentCaptor<CustomerBalanceHistory> captor = ArgumentCaptor.forClass(CustomerBalanceHistory.class);

        //when
        ChangeBalanceForm form = new ChangeBalanceForm("from","message",-20000);
        var result = customerBalanceService.changeBalance(1L,form);

        //then
        verify(customerBalanceHistoryRepository,times(1)).save(captor.capture());
        var historyCaptured= captor.getValue();
        assertEquals(30000-20000,historyCaptured.getCurrentMoney());
        assertEquals(-20000,historyCaptured.getChangeMoney());
        assertEquals("from",historyCaptured.getFromMessage());
        assertEquals("message",historyCaptured.getDescription());

    }

    @Test
    void changeBalanceSuccess_noLastHistory() {
        //given
        Customer customer = Customer.builder()
                .id(1L)
                .build();

        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customer.getId()))
                .willReturn(Optional.empty());

        ArgumentCaptor<CustomerBalanceHistory> captor = ArgumentCaptor.forClass(CustomerBalanceHistory.class);

        //when
        ChangeBalanceForm form = new ChangeBalanceForm("from","message",20000);
        var result = customerBalanceService.changeBalance(1L,form);

        //then
        verify(customerBalanceHistoryRepository,times(1)).save(captor.capture());
        var historyCaptured= captor.getValue();
        assertEquals(20000,historyCaptured.getCurrentMoney());
        assertEquals(20000,historyCaptured.getChangeMoney());
        assertEquals("from",historyCaptured.getFromMessage());
        assertEquals("message",historyCaptured.getDescription());
    }

    @Test
    void changeBalanceFail_noUser() {
        //given
        given(customerRepository.findById(1L)).willReturn(Optional.empty());

        //when
        //then
        try{
            ChangeBalanceForm form = new ChangeBalanceForm("from","message",20000);
            customerBalanceService.changeBalance(1L,form);

            throw new RuntimeException("예외가 발생하지 않음");
        }catch (Exception e){
            assertEquals(UNREGISTERED_USER.getDetail(),e.getMessage());
        }
    }

    @Test
    void changeBalanceFail_notEnoughBalance() {
        //given
        Customer customer = Customer.builder()
                .id(1L)
                .build();

        CustomerBalanceHistory history = CustomerBalanceHistory.builder()
                .customer(customer)
                .currentMoney(10000)
                .build();

        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));

        given(customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customer.getId()))
                .willReturn(Optional.of(history));

        //when
        //then
        try{
            ChangeBalanceForm form = new ChangeBalanceForm("from","message",-20000);
            customerBalanceService.changeBalance(1L,form);

            throw new RuntimeException("예외가 발생하지 않음");
        }catch (Exception e){
            assertEquals(NOT_ENOUGH_BALANCE.getDetail(),e.getMessage());
        }
    }
}