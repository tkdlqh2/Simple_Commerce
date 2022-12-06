package com.zerobase.cms.order.domain.redis;

import com.zerobase.cms.order.domain.product.AddProductCartForm;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@RedisHash("cart")
public class Cart {
    @Id
    private long customerId;
    private List<Product> products = new ArrayList<>();
    private List<String> messages = new ArrayList<>();

    public Cart(Long customerId){
        this.customerId = customerId;
    }

    public void addMessage(String message){
        messages.add(message);
    }

    public void addProduct(Product product){
        this.products.add(product);
    }

    public void addMessage(List<String> messages){
        messages.stream().forEach(this::addMessage);
    }

    public void initializeMessages(){
        this.messages = new ArrayList<>();
    }

    public void setProducts(List<Product> products){
        this.products = products;
    }

    public void removeProduct(Product product){
        this.products.remove(product);
    }
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product{
        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> items;

        public static Product from(AddProductCartForm form){
            return Product.builder()
                    .id(form.getProductId())
                    .sellerId(form.getSellerId())
                    .name(form.getName())
                    .description(form.getDescription())
                    .items(form.getItems().stream().map(ProductItem::from).collect(Collectors.toList()))
                    .build();
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem{
        private Long id;
        private String name;
        private Integer count;
        private Integer price;

        public static ProductItem from(AddProductCartForm.ProductItem form){
            return ProductItem.builder()
                    .id(form.getId())
                    .name(form.getName())
                    .count(form.getCount())
                    .price(form.getPrice())
                    .build();
        }

        public void setCount(Integer count){
            this.count = count;
        }

        public void setPrice(Integer price){
            this.price = price;
        }
    }
}
