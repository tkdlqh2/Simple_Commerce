package com.zerobase.cms.order.domain.respository;

import com.zerobase.cms.order.domain.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem,Long> {
}
