package com.zerobase.cms.zerobasecms.controller;

import com.zerobase.cms.zerobasecms.domain.customer.ChangeBalanceForm;
import com.zerobase.cms.zerobasecms.domain.customer.CustomerDto;
import com.zerobase.cms.zerobasecms.domain.model.Customer;
import com.zerobase.cms.zerobasecms.exception.CustomException;
import com.zerobase.cms.zerobasecms.exception.ErrorCode;
import com.zerobase.cms.zerobasecms.service.customer.CustomerBalanceService;
import com.zerobase.cms.zerobasecms.service.customer.CustomerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final JwtAuthenticationProvider provider;
    private final CustomerService customerService;
    private final CustomerBalanceService customerBalanceService;

    @GetMapping("/getInfo")
    public ResponseEntity<CustomerDto> getInfo(@RequestHeader(name = "X-AUTH-TOKEN") String token){
        UserVo vo = provider.getUser(token);
        Customer c = customerService.findByIdAndEmail(vo.getId(),vo.getEmail()).orElseThrow(
                ()-> new CustomException(ErrorCode.UNREGISTERED_USER)
        );

        return ResponseEntity.ok(CustomerDto.from(c));
    }

    @PostMapping("/balance")
    public ResponseEntity<Integer> changeBalance(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                                 @RequestBody ChangeBalanceForm form){
        UserVo vo = provider.getUser(token);

        return ResponseEntity.ok(customerBalanceService.changeBalance(vo.getId(),form).getCurrentMoney());
    }
}
