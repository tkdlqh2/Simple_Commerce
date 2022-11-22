package com.zerobase.cms.order.controller;

import com.zerobase.cms.order.domain.product.*;
import com.zerobase.cms.order.service.ProductItemService;
import com.zerobase.cms.order.service.ProductService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider provider;

    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                 @RequestBody AddProductForm form){

        return ResponseEntity.ok(ProductDto.from(productService.addProduct(provider.getUser(token).getId(),form)));
    }

    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                         @RequestBody AddProductItemForm form){

        return ResponseEntity.ok(ProductDto.from(productItemService.addProductItem(provider.getUser(token).getId(),form)));
    }

    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                 @RequestBody UpdateProductForm form){

        return ResponseEntity.ok(ProductDto.from(productService.updateProduct(provider.getUser(token).getId(),form)));
    }

    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> updateProductItem(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                            @RequestBody UpdateProductItemForm form){

        return ResponseEntity.ok(ProductItemDto.from(productItemService.updateProductItem(provider.getUser(token).getId(),form)));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                    @RequestParam Long id){
        productService.deleteProduct(provider.getUser(token).getId(),id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/item")
    public ResponseEntity<Void> deleteProductItem(@RequestHeader(name = "X_AUTH_TOKEN") String token,
                                                            @RequestParam Long id){
        productItemService.deleteProductItem(provider.getUser(token).getId(),id);
        return ResponseEntity.ok().build();
    }

}
