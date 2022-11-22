package com.zerobase.cms.order.domain.respository;

import com.zerobase.cms.order.domain.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {"productItems"},type = EntityGraph.EntityGraphType.LOAD)
    Optional<Product> findBySellerIdAndId(Long sellerId,Long Id);

    @EntityGraph(attributePaths = {"productItems"},type = EntityGraph.EntityGraphType.LOAD)
    Optional<Product> findWithProductItemsById(Long id);
}
