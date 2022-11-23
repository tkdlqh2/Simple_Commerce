package com.zerobase.cms.order.application;

import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CartApplicationTest {

    @Mock
    private ProductSearchService productSearchService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartApplication cartApplication;


    @Test
    void addCart() {
    }

    @Test
    void getCart() {
    }
}