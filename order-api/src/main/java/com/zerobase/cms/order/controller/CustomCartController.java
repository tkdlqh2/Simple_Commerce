package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.application.CartApplication;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer/cart")
@RequiredArgsConstructor
public class CustomCartController {

    private final JwtAuthenticationProvider provider;
    private final CartApplication cartApplication;

    @PostMapping
    public ResponseEntity<Cart> addCart(@RequestHeader(name = "X-AUTH-TOKEN") String token,
                                        @RequestBody AddProductCartForm form){

        return ResponseEntity.ok(cartApplication.addCart(provider.getUser(token).getId(),form));
    }

    @GetMapping
    public ResponseEntity<Cart> showCart(@RequestHeader(name = "X-AUTH-TOKEN") String token){
        return ResponseEntity.ok(cartApplication.getCart(provider.getUser(token).getId()));
    }


    @PutMapping
    public ResponseEntity<Cart> updateCart(@RequestHeader(name = "X-AUTH-TOKEN") String token
                    ,@RequestBody Cart cart){
        return ResponseEntity.ok(cartApplication.updateCart(provider.getUser(token).getId(),cart));
    }
}
