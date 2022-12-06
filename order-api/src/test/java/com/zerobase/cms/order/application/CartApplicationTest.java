package com.zerobase.cms.order.application;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.zerobase.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartApplicationTest {

    @Mock
    private ProductSearchService productSearchService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartApplication cartApplication;

    private static final Long PRODUCT_ID = 1L;
    private static final Long CUSTOMER_ID = 2L;
    private static final Long PRODUCT_ITEM_ID = 3L;
    private static final String NO_ERROR = "에러가 발생하지 않았습니다.";
    @Test
    void addCartSuccess() {
        //given
        Product product = Product.builder()
                .id(PRODUCT_ID)
                .productItems(Arrays.asList(ProductItem.builder()
                                .id(PRODUCT_ITEM_ID)
                                .count(4)
                                .build()))
                .build();

        given(productSearchService.getByProductId(PRODUCT_ID)).willReturn(product);

        AddProductCartForm form = AddProductCartForm.builder()
                .productId(PRODUCT_ID)
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                        .id(PRODUCT_ITEM_ID)
                        .count(2)
                        .build()))
                .build();

        Cart cart = new Cart(CUSTOMER_ID);
        cart.setProducts(Arrays.asList(Cart.Product.builder()
                        .id(PRODUCT_ID)
                        .items(Arrays.asList(
                                Cart.ProductItem.builder()
                                        .id(PRODUCT_ITEM_ID)
                                        .count(1)
                                        .build()
                        )).build()));
        given(cartService.getCart(CUSTOMER_ID)).willReturn(cart);
        given(cartService.addCart(CUSTOMER_ID,form)).willReturn(cart);

        //when
        var result = cartApplication.addCart(CUSTOMER_ID,form);
        //then
    }

    @Test
    void addCartFail_ProductNotFound() {
        //given
        given(productSearchService.getByProductId(PRODUCT_ID)).willReturn(null);

        AddProductCartForm form = AddProductCartForm.builder()
                .productId(PRODUCT_ID)
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                        .id(PRODUCT_ITEM_ID)
                        .count(2)
                        .build()))
                .build();
        //when
        //then
        try{
            cartApplication.addCart(CUSTOMER_ID,form);
            throw new RuntimeException(NO_ERROR);
        }catch(Exception e){
            assertEquals(NOT_FOUND_PRODUCT.getDetail(),e.getMessage());
        }
    }

    @Test
    void addCartFail_ItemNotEnough() {
        //given
        Product product = Product.builder()
                .id(PRODUCT_ID)
                .productItems(Arrays.asList(ProductItem.builder()
                        .id(PRODUCT_ITEM_ID)
                        .count(4)
                        .build()))
                .build();

        given(productSearchService.getByProductId(PRODUCT_ID)).willReturn(product);

        AddProductCartForm form = AddProductCartForm.builder()
                .productId(PRODUCT_ID)
                .items(Arrays.asList(AddProductCartForm.ProductItem.builder()
                        .id(PRODUCT_ITEM_ID)
                        .count(2)
                        .build()))
                .build();

        Cart cart = new Cart(CUSTOMER_ID);
        cart.setProducts(Arrays.asList(Cart.Product.builder()
                .id(PRODUCT_ID)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(PRODUCT_ITEM_ID)
                                .count(3)
                                .build()
                )).build()));
        given(cartService.getCart(CUSTOMER_ID)).willReturn(cart);
        //when
        //then
        try{
            cartApplication.addCart(CUSTOMER_ID,form);
            throw new RuntimeException(NO_ERROR);
        }catch(Exception e){
            assertEquals(ITEM_COUNT_NOT_ENOUGH.getDetail(),e.getMessage());
        }
    }

    @Test
    void refreshCart_NothingHappen(){
        //given
        List<Long> productIdList = Arrays.asList(1L,2L,3L);

        Product p1 = Product.builder()
                .id(1L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(20000)
                                .build()
                ))
                .build();
        Product p2 = Product.builder()
                .id(2L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(2L)
                                .count(6)
                                .price(30000)
                                .build(),
                        ProductItem.builder()
                                .id(3L)
                                .count(5)
                                .price(40000)
                                .build()
                ))
                .build();
        Product p3 = Product.builder()
                .id(3L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        given(productSearchService.getListByProductIds(productIdList)).willReturn(Arrays.asList(p1,p2,p3));
        Cart cart = new Cart(CUSTOMER_ID);

        Cart.Product cartP1 = Cart.Product.builder()
                .id(1L)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(1)
                                .price(20000)
                                .build()
                ))
                .build();
        Cart.Product cartP2 = Cart.Product.builder()
                .id(2L)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(2)
                                .price(40000)
                                .build()
                ))
                .build();
        Cart.Product cartP3 = Cart.Product.builder()
                .id(3L)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        cart.setProducts(Arrays.asList(cartP1,cartP2,cartP3));
        //when
        var result = cartApplication.refreshCart(cart);
        //then
        assertEquals(0,result.getMessages().size());
    }

    @Test
    void refreshCart_ProductDeleted(){
        //given
        List<Long> productIdList = Arrays.asList(1L,2L,3L);

        Product p1 = Product.builder()
                .id(1L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(20000)
                                .build()
                ))
                .build();

        Product p3 = Product.builder()
                .id(3L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        given(productSearchService.getListByProductIds(productIdList)).willReturn(Arrays.asList(p1,p3));
        Cart cart = new Cart(CUSTOMER_ID);

        Cart.Product cartP1 = Cart.Product.builder()
                .id(1L)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(1)
                                .price(20000)
                                .build()
                ))
                .build();
        Cart.Product cartP2 = Cart.Product.builder()
                .id(2L)
                .name("상품 2")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(2)
                                .price(40000)
                                .build()
                ))
                .build();
        Cart.Product cartP3 = Cart.Product.builder()
                .id(3L)
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        cart.addProduct(cartP1);
        cart.addProduct(cartP2);
        cart.addProduct(cartP3);
        //when
        var result = cartApplication.refreshCart(cart);
        //then
        assertEquals(1,result.getMessages().size());
        assertEquals("상품 2 상품이 제거되었습니다.",result.getMessages().get(0));
    }

    @Test
    void refreshCart_PriceChanged(){
        //given
        List<Long> productIdList = Arrays.asList(1L,2L,3L);

        Product p1 = Product.builder()
                .id(1L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(20000)
                                .build()
                ))
                .build();

        Product p2 = Product.builder()
                .id(2L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(2L)
                                .count(6)
                                .price(30000)
                                .build(),
                        ProductItem.builder()
                                .id(3L)
                                .count(5)
                                .price(40000)
                                .build()
                ))
                .build();

        Product p3 = Product.builder()
                .id(3L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        given(productSearchService.getListByProductIds(productIdList)).willReturn(Arrays.asList(p1,p2,p3));
        Cart cart = new Cart(CUSTOMER_ID);

        Cart.Product cartP1 = Cart.Product.builder()
                .id(1L)
                .name("상품 1")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(1)
                                .price(30000)
                                .build()
                ))
                .build();
        Cart.Product cartP2 = Cart.Product.builder()
                .id(2L)
                .name("상품 2")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(1)
                                .price(40000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(2)
                                .price(50000)
                                .build()
                ))
                .build();
        Cart.Product cartP3 = Cart.Product.builder()
                .id(3L)
                .name("상품 3")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(20000)
                                .build()
                ))
                .build();

        cart.addProduct(cartP1);
        cart.addProduct(cartP2);
        cart.addProduct(cartP3);
        //when
        var result = cartApplication.refreshCart(cart);
        //then
        assertEquals(4,result.getMessages().size());
        assertEquals("상품 1 가격이 변동되었습니다.",result.getMessages().get(0));
        assertEquals("상품 2 가격이 변동되었습니다.",result.getMessages().get(1));
        assertEquals("상품 2 가격이 변동되었습니다.",result.getMessages().get(2));
        assertEquals("상품 3 가격이 변동되었습니다.",result.getMessages().get(3));
    }

    @Test
    void refreshCart_NotEnoughCount(){
        //given
        List<Long> productIdList = Arrays.asList(1L,2L,3L);

        Product p1 = Product.builder()
                .id(1L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(1L)
                                .count(2)
                                .price(20000)
                                .build()
                ))
                .build();

        Product p2 = Product.builder()
                .id(2L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(2L)
                                .count(3)
                                .price(30000)
                                .build(),
                        ProductItem.builder()
                                .id(3L)
                                .count(1)
                                .price(40000)
                                .build()
                ))
                .build();

        Product p3 = Product.builder()
                .id(3L)
                .productItems(Arrays.asList(
                        ProductItem.builder()
                                .id(4L)
                                .count(1)
                                .price(10000)
                                .build()
                ))
                .build();

        given(productSearchService.getListByProductIds(productIdList)).willReturn(Arrays.asList(p1,p2,p3));
        Cart cart = new Cart(CUSTOMER_ID);

        Cart.Product cartP1 = Cart.Product.builder()
                .id(1L)
                .name("상품 1")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(1L)
                                .count(5)
                                .price(20000)
                                .build()
                ))
                .build();
        Cart.Product cartP2 = Cart.Product.builder()
                .id(2L)
                .name("상품 2")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(2L)
                                .count(6)
                                .price(30000)
                                .build(),
                        Cart.ProductItem.builder()
                                .id(3L)
                                .count(4)
                                .price(50000)
                                .build()
                ))
                .build();
        Cart.Product cartP3 = Cart.Product.builder()
                .id(3L)
                .name("상품 3")
                .items(Arrays.asList(
                        Cart.ProductItem.builder()
                                .id(4L)
                                .count(5)
                                .price(20000)
                                .build()
                ))
                .build();

        cart.addProduct(cartP1);
        cart.addProduct(cartP2);
        cart.addProduct(cartP3);
        //when
        var result = cartApplication.refreshCart(cart);
        //then
        assertEquals(4,result.getMessages().size());
        var resultMessages = result.getMessages();
        assertEquals(4, resultMessages.size());
        assertEquals("수량이 부족하여 구매 가능한 최대치로 변경되었습니다.",resultMessages.get(0));
        assertEquals("수량이 부족하여 구매 가능한 최대치로 변경되었습니다.",resultMessages.get(1));
        assertEquals("상품 2 가격이 변동되었고, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.",resultMessages.get(2));
        assertEquals("상품 3 가격이 변동되었고, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.",resultMessages.get(3));
        for (String message: result.getMessages()) {
            System.out.println(message);
        }
        var resultProducts = result.getProducts();
        assertEquals(2,resultProducts.get(0).getItems().get(0).getCount());
        assertEquals(3,resultProducts.get(1).getItems().get(0).getCount());
        assertEquals(1,resultProducts.get(1).getItems().get(1).getCount());
        assertEquals(1,resultProducts.get(2).getItems().get(0).getCount());
    }

}