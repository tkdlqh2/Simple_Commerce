package com.zerobase.cms.order.application;

import com.zerobase.cms.order.client.UserClient;
import com.zerobase.cms.order.client.user.ChangeBalanceForm;
import com.zerobase.cms.order.client.user.CustomerDto;
import com.zerobase.cms.order.domain.model.ProductItem;
import com.zerobase.cms.order.domain.redis.Cart;
import com.zerobase.cms.order.exception.CustomException;
import com.zerobase.cms.order.service.ProductItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.stream.IntStream;

import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.cms.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

@Service
@RequiredArgsConstructor
public class OrderApplication {

    private final CartApplication cartApplication;
    private final ProductItemService productItemService;
    private final UserClient userClient;

    @Transactional(noRollbackFor = CustomException.class,isolation = Isolation.REPEATABLE_READ)
    public void order(String token, Cart cart){

        Cart orderCart = cartApplication.refreshCart(cart);
        if(orderCart.getMessages().size()>0){
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();
        var customerId = customerDto.getId();

        Cart myCart = cartApplication.getCart(customerId);
        int totalPrice= getTotalPrice(cart);
        if(customerDto.getBalance() < totalPrice){
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }

        userClient.changeBalance(token,ChangeBalanceForm.builder()
                .from("USER")
                        .message("Order")
                        .money(-totalPrice)
                .build());

        HashMap <Long,Cart.Product> myProductMap = new HashMap<>();
        HashMap <Long,Cart.ProductItem> myProductItemMap = new HashMap<>();
        for(Cart.Product myCartProduct : myCart.getProducts()){
            myProductMap.put(myCartProduct.getId(),myCartProduct);
            for(Cart.ProductItem myCartProductItem : myCartProduct.getItems()){
                myProductItemMap.put(myCartProductItem.getId(),myCartProductItem);
            }
        }

        for(Cart.Product product : orderCart.getProducts()){
            var myCartProduct = myProductMap.get(product.getId());
            for(Cart.ProductItem cartItem : product.getItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount()-cartItem.getCount());
                var myCartProductItem = myProductItemMap.get(productItem.getId());
                if(myCartProductItem.getCount()-cartItem.getCount() == 0){
                    myCartProduct.removeItem(myCartProductItem);
                    if(myCartProduct.getItems().size() == 0){myCart.removeProduct(myCartProduct);}
                }else{
                    myCartProductItem.setCount(myCartProductItem.getCount()-cartItem.getCount());
                }
            }
        }
        cartApplication.updateCart(customerId,myCart);
    }

    private Integer getTotalPrice(Cart cart){

        return cart.getProducts().stream().flatMapToInt(
                product-> product.getItems().stream().flatMapToInt(productItem ->
                        IntStream.of(productItem.getPrice()*productItem.getCount())))
                        .sum();
    }
}
