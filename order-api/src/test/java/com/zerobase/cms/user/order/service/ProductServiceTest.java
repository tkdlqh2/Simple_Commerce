package com.zerobase.cms.user.order.service;

import com.zerobase.cms.user.order.domain.model.Product;
import com.zerobase.cms.user.order.domain.product.AddProductForm;
import com.zerobase.cms.user.order.domain.product.AddProductItemForm;
import com.zerobase.cms.user.order.domain.respository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void ADD_PRODUCT_TEST(){
        Long sellerId = 1L;

        AddProductForm form = makeProductForm("나이키 에어포스","좋은 신발",5);
        Product p = productService.addProduct(sellerId,form);

        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);
        assertEquals(result.getName(),"나이키 에어포스");
        assertEquals(result.getDescription(),"좋은 신발");
        assertEquals(result.getProductItems().size(),5);
        assertEquals(result.getProductItems().get(0).getName(),"나이키 에어포스0");
        assertEquals(result.getProductItems().get(0).getPrice(),1000);
        assertEquals(result.getProductItems().get(0).getCount(),1);
    }

    private static AddProductForm makeProductForm(String name, String description,int itemCount){
        List<AddProductItemForm> itemForms = new ArrayList<>();
        for(int i=0;i<itemCount;i++){
            itemForms.add(makeProductItemForm(null,name+i));
        }
        return AddProductForm.builder()
                .name(name)
                .description(description)
                .items(itemForms)
                .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name){
        return AddProductItemForm.builder()
                .productId(productId)
                .name(name)
                .price(1000)
                .count(1)
                .build();
    }
}