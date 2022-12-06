package com.zerobase.cms.order.domain.model;

import com.zerobase.cms.order.domain.product.AddProductItemForm;
import com.zerobase.cms.order.domain.product.UpdateProductItemForm;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sellerId;
    @Audited
    private String name;
    @Audited
    private Integer price;
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductItem of(Long sellerId, AddProductItemForm form){
        return ProductItem.builder()
                .sellerId(sellerId)
                .name(form.getName())
                .price(form.getPrice())
                .count(form.getCount())
                .build();
    }

    public void updateFromForm(UpdateProductItemForm form){
        this.name = form.getName();
        this.count = form.getCount();
        this.price = form.getPrice();
    }

    //test를 위해 (더 나은 대안은 무엇일까요? ㅠㅠ)
    public void setId(Long id){
        this.id = id;
    }

    public void setCount(Integer count){
        this.count = count;
    }
}
