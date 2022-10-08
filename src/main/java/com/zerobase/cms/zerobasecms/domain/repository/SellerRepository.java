package com.zerobase.cms.zerobasecms.domain.repository;

import com.zerobase.cms.zerobasecms.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller> findByEmail(String email);
}
