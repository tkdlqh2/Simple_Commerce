package com.zerobase.cms.user.order.service;

import com.zerobase.cms.user.order.domain.model.Product;
import com.zerobase.cms.user.order.domain.model.ProductItem;
import com.zerobase.cms.user.order.domain.product.AddProductForm;
import com.zerobase.cms.user.order.domain.product.UpdateProductForm;
import com.zerobase.cms.user.order.domain.product.UpdateProductItemForm;
import com.zerobase.cms.user.order.domain.respository.ProductRepository;
import com.zerobase.cms.user.order.exception.CustomException;
import com.zerobase.cms.user.order.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addProduct(Long sellerId, AddProductForm form){
        return productRepository.save(Product.of(sellerId, form));
    }

    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm form){
        Product product = productRepository.findBySellerIdAndId(sellerId,form.getId())
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
        product.setName(form.getName());
        product.setDescription(form.getDescription());

        for(UpdateProductItemForm itemForm: form.getItems()){
            ProductItem productItem = product.getProductItems().stream()
                            .filter(pi->pi.getId().equals(itemForm.getId()))
                                    .findFirst().orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_ITEM));
            productItem.setName(itemForm.getName());
            productItem.setCount(itemForm.getCount());
            productItem.setPrice(itemForm.getPrice());
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
