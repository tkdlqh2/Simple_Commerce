package com.zerobase.cms.order.domain.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductItemForm {
    private Long productId;
    private String name;
    private Integer price;
    private Integer count;

}
