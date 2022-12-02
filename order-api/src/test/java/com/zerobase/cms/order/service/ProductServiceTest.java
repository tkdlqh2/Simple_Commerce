package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.respository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void addProductSuccess(){


        //given
        AddProductForm form = makeProductForm("나이키 에어포스","좋은 신발",5);
        Product p = Product.of(1L,form);

        var instance = mockStatic(Product.class);
        given(Product.of(1L,form)).willReturn(p);
        given(productRepository.save(p)).willReturn(p);

        //when
        var result = productService.addProduct(1L,form);

        //then
        assertNotNull(result);
        assertEquals(result.getName(),"나이키 에어포스");
        assertEquals(result.getDescription(),"좋은 신발");
        assertEquals(result.getProductItems().size(),5);
        assertEquals(result.getProductItems().get(0).getName(),"나이키 에어포스0");
        assertEquals(result.getProductItems().get(0).getPrice(),1000);
        assertEquals(result.getProductItems().get(0).getCount(),1);

        instance.close();
    }

    @Test
    void updateProductSuccess(){

        //given
        AddProductForm addForm = makeProductForm("나이키 에어포스","좋은 신발",5);
        Product p = Product.of(1L,addForm);
        Long idx=0L;
        for(ProductItem item: p.getProductItems()){
            item.setId(idx);
            idx++;
        }

        given(productRepository.findBySellerIdAndId(1L,1L)).willReturn(Optional.of(p));

        UpdateProductForm form = updateProductForm("나이키 에어포스 수정본","더 좋은 신발",5);

        //when
        var result = productService.updateProduct(1L,form);

        //then
        assertNotNull(result);
        assertEquals(result.getName(),"나이키 에어포스 수정본");
        assertEquals(result.getDescription(),"더 좋은 신발");
        assertEquals(result.getProductItems().size(),5);
        assertEquals(result.getProductItems().get(0).getName(),"나이키 에어포스 수정본0");
        assertEquals(result.getProductItems().get(0).getPrice(),2000);
        assertEquals(result.getProductItems().get(0).getCount(),2);
    }

    @Test
    void updateProductFail_NoProduct(){

        //given
        given(productRepository.findBySellerIdAndId(1L,1L)).willReturn(Optional.empty());
        UpdateProductForm form = updateProductForm("오캬캬","설명",2);

        //when
        //then
        try{
            productService.updateProduct(1L,form);
            throw new RuntimeException("에러가 발생하지 않음");
        } catch (Exception e){
            assertEquals(NOT_FOUND_PRODUCT.getDetail(),e.getMessage());
        }

    }

    @Test
    void updateProductFail_ProductItemNotFound(){

        //given
        AddProductForm addForm = makeProductForm("나이키 에어포스","좋은 신발",5);
        Product p = Product.of(1L,addForm);
        Long idx=5L;
        for(ProductItem item: p.getProductItems()){
            item.setId(idx);
            idx++;
        }

        given(productRepository.findBySellerIdAndId(1L,1L)).willReturn(Optional.of(p));
        UpdateProductForm form = updateProductForm("실패할 상품 명","",3);

        //when
        //then
        try{
            productService.updateProduct(1L,form);
            throw new RuntimeException("에러가 발생하지 않음");
        } catch (Exception e){
            assertEquals(NOT_FOUND_ITEM.getDetail(),e.getMessage());
        }
    }

    private AddProductForm makeProductForm(String name, String description,int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for(int i=0;i<itemCount;i++){
            itemForms.add(makeProductItemForm(name+i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private AddProductItemForm makeProductItemForm(String name){
        return AddProductItemForm.builder()
                .name(name)
                .price(1000)
                .count(1)
                .build();
    }

    private UpdateProductForm updateProductForm(String name, String description,int itemCount){
        List<UpdateProductItemForm> itemForms = new ArrayList<>();
        Long itemIdx = 0L;
        for(int i=0;i<itemCount;i++){
            itemForms.add(updateProductItemForm(itemIdx,name+i));
            itemIdx++;
        }
        return UpdateProductForm.builder()
                .id(1L)
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private UpdateProductItemForm updateProductItemForm(Long productItemId, String name){
        return UpdateProductItemForm.builder()
                .productItemId(productItemId)
                .name(name)
                .price(2000)
                .count(2)
                .build();
    }
}