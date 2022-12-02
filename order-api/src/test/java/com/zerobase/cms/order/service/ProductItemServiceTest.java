package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.respository.ProductItemRepository;
import com.zerobase.cms.order.domain.respository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.zerobase.cms.order.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductItemServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductItemRepository productItemRepository;
    @InjectMocks
    private ProductItemService productItemService;

    private static final Long SELLER_ID = 1L;
    private static final Long PRODUCT_ITEM_ID = 1L;
    private static final String NO_ERROR = "에러가 발생하지 않았습니다.";

    @Test
    void addProductItemSuccess() {
        //given
        Product product = Product.builder()
                .id(2L)
                .productItems(new ArrayList<>())
                .build();

        given(productRepository.findBySellerIdAndId(SELLER_ID,2L))
                .willReturn(Optional.of(product));

        //when
        AddProductItemForm form = AddProductItemForm.builder()
                .productId(2L)
                .name("새 상품")
                .count(2)
                .build();

        var result = productItemService.addProductItem(SELLER_ID,form);
        //then
        assertEquals(1,result.getProductItems().size());
        assertEquals("새 상품", result.getProductItems().get(0).getName());
        assertEquals(2, result.getProductItems().get(0).getCount());
    }

    @Test
    void addProductItemFail_NotFoundProduct() {
        //given
        given(productRepository.findBySellerIdAndId(SELLER_ID,2L))
                .willReturn(Optional.empty());

        //when
        //then
        AddProductItemForm form = AddProductItemForm.builder()
                .productId(2L)
                .name("새 상품")
                .count(2)
                .build();
        try{
            productItemService.addProductItem(SELLER_ID,form);
            throw new RuntimeException(NO_ERROR);
        }catch (Exception e){
            assertEquals(NOT_FOUND_PRODUCT.getDetail(),e.getMessage());
        }

    }

    @Test
    void addProductItemFail_SameNameProductItem() {
        //given
        ProductItem existedItem = ProductItem.builder()
                .name("기존 상품")
                .count(2)
                .build();

        Product product = Product.builder()
                .id(2L)
                .productItems(Arrays.asList(existedItem))
                .build();

        given(productRepository.findBySellerIdAndId(SELLER_ID,2L))
                .willReturn(Optional.of(product));
        //when
        //then
        AddProductItemForm form = AddProductItemForm.builder()
                .productId(2L)
                .name("기존 상품")
                .count(2)
                .build();
        try{
            productItemService.addProductItem(SELLER_ID,form);
            throw new RuntimeException(NO_ERROR);
        }catch (Exception e){
            assertEquals(SAME_ITEM_NAME.getDetail(),e.getMessage());
        }
    }

    @Test
    void updateProductItemSuccess() {
        //given
        ProductItem productItem = ProductItem.builder()
                .id(PRODUCT_ITEM_ID)
                .sellerId(SELLER_ID)
                .name("기존 이름")
                .count(2)
                .price(3000)
                .build();

        UpdateProductItemForm form = UpdateProductItemForm.builder()
                .productItemId(PRODUCT_ITEM_ID)
                .name("새 이름")
                .count(5)
                .price(2000)
                .build();

        given(productItemRepository.findById(PRODUCT_ITEM_ID)).willReturn(Optional.of(productItem));
        //when
        var result = productItemService.updateProductItem(SELLER_ID,form);
        //then
        assertEquals(PRODUCT_ITEM_ID,result.getId());
        assertEquals("새 이름",result.getName());
        assertEquals(5, result.getCount());
        assertEquals(2000, result.getPrice());
    }

    @Test
    void updateProductItemFail_NoItem() {
        //given
        UpdateProductItemForm form = UpdateProductItemForm.builder()
                .productItemId(PRODUCT_ITEM_ID)
                .name("새 이름")
                .count(5)
                .price(2000)
                .build();

        given(productItemRepository.findById(PRODUCT_ITEM_ID)).willReturn(Optional.empty());
        //when
        //then
        try{
            productItemService.updateProductItem(SELLER_ID,form);
            throw new RuntimeException(NO_ERROR);
        }catch (Exception e){
            assertEquals(NOT_FOUND_ITEM.getDetail(),e.getMessage());
        }
    }

    @Test
    void deleteProductItemSuccess() {
        //given
        ProductItem productItem = ProductItem.builder()
                .id(PRODUCT_ITEM_ID)
                .sellerId(SELLER_ID)
                .name("기존 이름")
                .count(2)
                .price(3000)
                .build();

        given(productItemRepository.findById(PRODUCT_ITEM_ID)).willReturn(Optional.of(productItem));
        ArgumentCaptor<ProductItem> captor = ArgumentCaptor.forClass(ProductItem.class);

        //when
        productItemService.deleteProductItem(SELLER_ID,PRODUCT_ITEM_ID);
        //then
        verify(productItemRepository,times(1)).delete(captor.capture());
        var capturedItem = captor.getValue();
        assertEquals(productItem,capturedItem);
    }

    @Test
    void deleteProductItemFail_NoProductItem() {
        //given
        given(productItemRepository.findById(PRODUCT_ITEM_ID)).willReturn(Optional.empty());

        //when
        //then
        try{
            productItemService.deleteProductItem(SELLER_ID,PRODUCT_ITEM_ID);
            throw new RuntimeException(NO_ERROR);
        }catch (Exception e){
            assertEquals(NOT_FOUND_ITEM.getDetail(),e.getMessage());
        }
    }
}