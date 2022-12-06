package com.zerobase.cms.order.service;

import com.zerobase.cms.order.client.RedisClient;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private RedisClient redisClient;
    @InjectMocks
    private CartService cartService;

    private final static Long CUSTOMER_ID = 1L;
    private final static Long SELLER_ID = 3L;
    private final static Long PRODUCT_ID = 2L;



    @Test
    void addCart_NewCart() {
        //given
        AddProductCartForm form = AddProductCartForm.builder()
                .name("물건 1")
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                                .id(2L)
                                .name("물건 1-옵션 1")
                                .count(2)
                                .price(20000)
                        .build(),AddProductCartForm.ProductItem.builder()
                        .id(3L)
                        .name("물건 1-옵션 2")
                        .count(3)
                        .price(30000)
                        .build()))
                .sellerId(SELLER_ID)
                .productId(PRODUCT_ID)
                .build();

        given(redisClient.get(CUSTOMER_ID,Cart.class)).willReturn(null);
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        //when
        var result = cartService.addCart(CUSTOMER_ID,form);

        //then
        verify(redisClient,times(1)).put(eq(CUSTOMER_ID),captor.capture());
        assertEquals(0,result.getMessages().size());
        assertEquals(1,result.getProducts().size());
        Cart.Product resultProduct = result.getProducts().get(0);
        assertEquals("물건 1",resultProduct.getName());
        assertEquals(2,resultProduct.getItems().size());
        assertEquals("물건 1-옵션 1",resultProduct.getItems().get(0).getName());
        assertEquals(2,resultProduct.getItems().get(0).getCount());
        assertEquals(20000,resultProduct.getItems().get(0).getPrice());
        assertEquals("물건 1-옵션 2",resultProduct.getItems().get(1).getName());
        assertEquals(3,resultProduct.getItems().get(1).getCount());
        assertEquals(30000,resultProduct.getItems().get(1).getPrice());
    }

    @Test
    void addCart_AlreadyCartExisting() {
        //given
        Cart cart = new Cart(CUSTOMER_ID);
        cart.addProduct(Cart.Product.builder()
                .id(PRODUCT_ID+1)
                .name("물건 2")
                .sellerId(SELLER_ID)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(4L)
                                .count(4)
                                .price(40000)
                                .name("물건 2-옵션 1")
                                .build(),
                        Cart.ProductItem.builder()
                                .id(5L)
                                .count(5)
                                .price(50000)
                                .name("물건 2-옵션 2")
                                .build()))
                .build());

        cart.addProduct(Cart.Product.builder()
                .id(PRODUCT_ID+2)
                .name("물건 3")
                .sellerId(SELLER_ID)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(5L)
                                .count(5)
                                .price(50000)
                                .name("물건 3-옵션 1")
                                .build(),
                        Cart.ProductItem.builder()
                                .id(6L)
                                .count(6)
                                .price(60000)
                                .name("물건 3-옵션 2")
                                .build())).build());

        AddProductCartForm form = AddProductCartForm.builder()
                .name("물건 1")
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                        .id(2L)
                        .name("물건 1-옵션 1")
                        .count(2)
                        .price(20000)
                        .build(),AddProductCartForm.ProductItem.builder()
                        .id(3L)
                        .name("물건 1-옵션 2")
                        .count(3)
                        .price(30000)
                        .build()))
                .sellerId(SELLER_ID)
                .productId(PRODUCT_ID)
                .build();
        given(redisClient.get(CUSTOMER_ID,Cart.class)).willReturn(cart);
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        //when
        var result = cartService.addCart(CUSTOMER_ID,form);

        //then
        verify(redisClient,times(1)).put(eq(CUSTOMER_ID),captor.capture());
        assertEquals(0,result.getMessages().size());
        assertEquals(3,result.getProducts().size());
        Cart.Product resultProduct = result.getProducts().get(2);
        assertEquals("물건 1",resultProduct.getName());
        assertEquals(2,resultProduct.getItems().size());
        assertEquals("물건 1-옵션 1",resultProduct.getItems().get(0).getName());
        assertEquals(2,resultProduct.getItems().get(0).getCount());
        assertEquals(20000,resultProduct.getItems().get(0).getPrice());
        assertEquals("물건 1-옵션 2",resultProduct.getItems().get(1).getName());
        assertEquals(3,resultProduct.getItems().get(1).getCount());
        assertEquals(30000,resultProduct.getItems().get(1).getPrice());
    }

    @DisplayName("Cart 기존 Item의 정보와 AddProductForm 의 정보가 다를 때 -> form의 정보를 따라간다.")
    @Test
    void addCart_AlreadyExistingItem() {
        //given
        Cart cart = new Cart(CUSTOMER_ID);
        cart.addProduct(Cart.Product.builder()
                .id(PRODUCT_ID)
                .name("물건 1")
                .sellerId(SELLER_ID)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(4)
                                .price(40000)
                                .name("물건 1-옵션 1")
                                .build(),
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(5)
                                .price(50000)
                                .name("물건 1-옵션 2")
                                .build()))
                .build());

        cart.addProduct(Cart.Product.builder()
                .id(PRODUCT_ID+2)
                .name("물건 3")
                .sellerId(SELLER_ID)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(5L)
                                .count(5)
                                .price(50000)
                                .name("물건 3- 옵션 1")
                                .build(),
                        Cart.ProductItem.builder()
                                .id(6L)
                                .count(6)
                                .price(60000)
                                .name("물건 3- 옵션 2")
                                .build())).build());

        AddProductCartForm form = AddProductCartForm.builder()
                .name("물건 1 변경됨")
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                        .id(2L)
                        .name("물건 1-옵션 1")
                        .count(2)
                        .price(20000)
                        .build(),AddProductCartForm.ProductItem.builder()
                        .id(3L)
                        .name("물건 1-옵션 2")
                        .count(3)
                        .price(30000)
                        .build()))
                .sellerId(SELLER_ID)
                .productId(PRODUCT_ID)
                .build();
        given(redisClient.get(CUSTOMER_ID,Cart.class)).willReturn(cart);
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        //when
        var result = cartService.addCart(CUSTOMER_ID,form);

        //then
        verify(redisClient,times(1)).put(eq(CUSTOMER_ID),captor.capture());
        assertEquals(3,result.getMessages().size());
        assertEquals("물건 1의 정보가 물건 1 변경됨(으)로 변경되었습니다. 확인 부탁드립니다.",result.getMessages().get(0));
        assertEquals("물건 1 변경됨 물건 1-옵션 1의 가격이 변경되었습니다.",result.getMessages().get(1));
        assertEquals("물건 1 변경됨 물건 1-옵션 2의 가격이 변경되었습니다.",result.getMessages().get(2));
        assertEquals(2,result.getProducts().size());
        Cart.Product resultProduct = result.getProducts().get(0);
        assertEquals("물건 1 변경됨",resultProduct.getName());
        assertEquals(2,resultProduct.getItems().size());
        assertEquals("물건 1-옵션 1",resultProduct.getItems().get(0).getName());
        assertEquals(6,resultProduct.getItems().get(0).getCount());
        assertEquals(20000,resultProduct.getItems().get(0).getPrice());
        assertEquals("물건 1-옵션 2",resultProduct.getItems().get(1).getName());
        assertEquals(8,resultProduct.getItems().get(1).getCount());
        assertEquals(30000,resultProduct.getItems().get(1).getPrice());
    }
}