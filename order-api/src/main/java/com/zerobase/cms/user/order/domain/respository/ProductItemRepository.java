package com.zerobase.cms.user.order.domain.respository;

import com.zerobase.cms.user.order.domain.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem,Long> {
}
