package com.zerobase.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductCartForm {
    private Long productId;
    private Long sellerId;
    private String name;
    private String description;
    private List<ProductItem> items;

    public static class ProductItem{
        private Long id;
        private String name;
        private Integer count;
        private Integer price;
    }
}
