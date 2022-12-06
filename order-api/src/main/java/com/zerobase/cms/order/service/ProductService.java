package com.zerobase.cms.order.service;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.order.domain.respository.ProductRepository;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form){
        var product  = Product.of(sellerId, form);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form){
        Product product = productRepository.findBySellerIdAndId(sellerId,form.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        product.updateByForm(form);

        for(UpdateProductItemForm itemForm: form.getItems()){
            ProductItem productItem = product.getProductItems().stream()
                            .filter(pi->pi.getId().equals(itemForm.getProductItemId()))
                                    .findFirst().orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_ITEM));
            productItem.updateFromForm(itemForm);
        }
        return product;
    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId){
        Product product = productRepository.findBySellerIdAndId(sellerId,productId)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        productRepository.delete(product);
    }
}
