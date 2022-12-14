package com.zerobase.cms.order.application;

import com.zerobase.cms.order.domain.model.Product;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.product.AddProductCartForm;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.service.CartService;
import com.zerobase.cms.order.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.zerobase.cms.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.zerobase.cms.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

@Service
@RequiredArgsConstructor
public class CartApplication {
    private final ProductSearchService productSearchService;
    private final CartService cartService;

    public Cart addCart(Long customerId, AddProductCartForm form){

        Product product = productSearchService.getByProductId(form.getProductId());
        if(product == null){
            throw new CustomException(NOT_FOUND_PRODUCT);
        }

        Cart cart = cartService.getCart(customerId);

        if(cart != null && !addAble(cart,product,form)){
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }else{
            return cartService.addCart(customerId, form);
        }
    }


    public Cart getCart(Long customerId){
        Cart cart  = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(),cart);
        Cart returnCart = new Cart(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.addMessage(cart.getMessages());
        cart.initializeMessages();
        cartService.putCart(customerId,cart);
        return returnCart;
    }

    public Cart updateCart(Long customerId, Cart cart){
        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    protected Cart refreshCart(Cart cart){
       Map<Long,Product> productMap = productSearchService.getListByProductIds(new ArrayList<>(cart.getProducts().stream().map(
                       Cart.Product::getId).collect(Collectors.toList()))).stream()
               .collect(Collectors.toMap(Product::getId, product -> product));

       for(int i=0; i<cart.getProducts().size();i++){
           Cart.Product cartProduct = cart.getProducts().get(i);

           Product p = productMap.get(cartProduct.getId());
           if(p == null){
               cart.removeProduct(cartProduct);
               i--;
               cart.addMessage(cartProduct.getName() + " ????????? ?????????????????????.");
               continue;
           }

           Map<Long,ProductItem> productItemMap = p.getProductItems().stream()
                   .collect(Collectors.toMap(ProductItem::getId,productItem -> productItem));
           for(int j=0; j<cartProduct.getItems().size();j++){

               Cart.ProductItem cartProductItem = cartProduct.getItems().get(j);
               ProductItem pi = productItemMap.get(cartProductItem.getId());
               if(pi == null){
                   cartProduct.getItems().remove(cartProductItem);
                   j--;
                   cart.addMessage(cartProduct.getName()+"??? "+cartProductItem.getName()+"????????? ?????????????????????.");
               }

                boolean isPriceChanged = false,isCountNotEnough = false;
                if(!cartProductItem.getPrice().equals(pi.getPrice())){
                    isPriceChanged = true;
                    cartProductItem.setPrice(pi.getPrice());
                }
                if(cartProductItem.getCount() > pi.getCount()){
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }
                if(isPriceChanged && isCountNotEnough){
                    cart.addMessage(cartProduct.getName()+" ????????? ???????????????, ????????? ???????????? ?????? ????????? ???????????? ?????????????????????.");
                }else if(isPriceChanged){
                    cart.addMessage(cartProduct.getName()+" ????????? ?????????????????????.");
                }else if(isCountNotEnough){
                    cart.addMessage("????????? ???????????? ?????? ????????? ???????????? ?????????????????????.");
                }


                if(cartProduct.getItems().size() == 0) {
                    cart.removeProduct(cartProduct);
                    i--;
                    cart.addMessage(cartProduct.getName() + " ????????? ????????? ?????? ????????? ????????? ??????????????????.");
                }
           }
       }
        return cart;
    }

    private boolean addAble(Cart cart,Product product,AddProductCartForm form){
        Cart.Product cartProduct = cart.getProducts().stream()
                .filter(p -> p.getId().equals(form.getProductId()))
                .findFirst().orElse(Cart.Product.builder().id(product.getId())
                        .items(Collections.emptyList()).build());

        Map<Long,Integer> cartItemCountMap = cartProduct.getItems().stream()
                .collect(Collectors.toMap(Cart.ProductItem::getId,Cart.ProductItem::getCount));

        Map<Long,Integer> currentItemCountMap = product.getProductItems().stream()
                .collect(Collectors.toMap(ProductItem::getId,ProductItem::getCount));

        return form.getItems().stream().noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if(cartCount == null){
                        cartCount = 0;
                    }
                    Integer currentCount = currentItemCountMap.get(formItem.getId());
                    return formItem.getCount() + cartCount > currentCount;
                });
    }
}

