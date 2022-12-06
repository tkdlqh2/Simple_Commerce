package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.service.ProductItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApplicationTest {

    @Mock
    private CartApplication cartApplication;
    @Mock
    private ProductItemService productItemService;
    @Mock
    private UserClient userClient;
    @InjectMocks
    private OrderApplication orderApplication;

    private static final Long CUSTOMER_ID = 1L;
    private static final String NO_ERROR = "에러가 발생하지 않았습니다.";

    @Test
    void orderSuccess() {
        //given
        Cart orderCart = new Cart(CUSTOMER_ID);
        Cart.Product p1 = Cart.Product.builder()
                .id(1L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(2)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(10000)
                                .build()
                        ).collect(Collectors.toList()))
                .build();

        Cart.Product p2 = Cart.Product.builder()
                .id(2L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(3)
                                .price(30000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        orderCart.addProduct(p1);
        orderCart.addProduct(p2);

        Cart wholeCart = new Cart(CUSTOMER_ID);
        Cart.Product p1Whole = Cart.Product.builder()
                .id(1L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(10000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        Cart.Product p2Whole = Cart.Product.builder()
                .id(2L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(4)
                                .price(30000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        wholeCart.addProduct(p1Whole);
        wholeCart.addProduct(p2Whole);

        ProductItem productItem1 = ProductItem.builder()
                .id(1L)
                .count(300)
                .build();

        ProductItem productItem2 = ProductItem.builder()
                .id(2L)
                .count(300)
                .build();

        ProductItem productItem3 = ProductItem.builder()
                .id(3L)
                .count(300)
                .build();


        CustomerDto customerDto = new CustomerDto(CUSTOMER_ID,"abc@gmail.com",10000000);

        ArgumentCaptor<ChangeBalanceForm> changeBalanceCaptor = ArgumentCaptor.forClass(ChangeBalanceForm.class);
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        given(cartApplication.refreshCart(any())).willReturn(orderCart);
        given(userClient.getCustomerInfo(anyString())).willReturn(ResponseEntity.ok(customerDto));
        given(cartApplication.getCart(CUSTOMER_ID)).willReturn(wholeCart);
        given(productItemService.getProductItem(1L)).willReturn(productItem1);
        given(productItemService.getProductItem(2L)).willReturn(productItem2);
        given(productItemService.getProductItem(3L)).willReturn(productItem3);


        //when
        orderApplication.order("token",orderCart);
        //then
        verify(userClient,times(1)).changeBalance(any(),changeBalanceCaptor.capture());
        assertEquals(-160000,changeBalanceCaptor.getValue().getMoney());
        verify(cartApplication,times(1)).updateCart(eq(CUSTOMER_ID),cartCaptor.capture());
        List<Cart.Product> capturedCartProducts = cartCaptor.getValue().getProducts();
        assertEquals(2,capturedCartProducts.size());
        assertEquals(5-2,capturedCartProducts.get(0).getItems().get(0).getCount());
        assertEquals(4-3,capturedCartProducts.get(1).getItems().get(0).getCount());
    }

    @Test
    void orderFail_ErrorWhileUpdate() {
        //given
        Cart orderCart = new Cart(CUSTOMER_ID);
        orderCart.addMessage("오류 발생");

        given(cartApplication.refreshCart(any())).willReturn(orderCart);

        //when
        //then
        try{
            orderApplication.order("token",orderCart);
            throw new RuntimeException(NO_ERROR);
        } catch (Exception e){
            assertEquals(ORDER_FAIL_CHECK_CART.getDetail(),e.getMessage());
        }
    }

    @Test
    void orderFail_NotEnoughBalance() {
        //given
        Cart orderCart = new Cart(CUSTOMER_ID);
        Cart.Product p1 = Cart.Product.builder()
                .id(1L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(2)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(10000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        Cart.Product p2 = Cart.Product.builder()
                .id(2L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(3)
                                .price(30000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        orderCart.addProduct(p1);
        orderCart.addProduct(p2);

        Cart wholeCart = new Cart(CUSTOMER_ID);
        Cart.Product p1Whole = Cart.Product.builder()
                .id(1L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(10000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        Cart.Product p2Whole = Cart.Product.builder()
                .id(2L)
                .items(Stream.of(
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(4)
                                .price(30000)
                                .build()
                ).collect(Collectors.toList()))
                .build();

        wholeCart.addProduct(p1Whole);
        wholeCart.addProduct(p2Whole);

        CustomerDto customerDto = new CustomerDto(CUSTOMER_ID,"abc@gmail.com",10000);

        given(cartApplication.refreshCart(any())).willReturn(orderCart);
        given(userClient.getCustomerInfo(anyString())).willReturn(ResponseEntity.ok(customerDto));
        given(cartApplication.getCart(CUSTOMER_ID)).willReturn(wholeCart);

        //when
        //then
        try{
            orderApplication.order("token",orderCart);
            throw new RuntimeException(NO_ERROR);
        }catch (Exception e){
            assertEquals(ORDER_FAIL_NO_MONEY.getDetail(),e.getMessage());
        }
    }
}