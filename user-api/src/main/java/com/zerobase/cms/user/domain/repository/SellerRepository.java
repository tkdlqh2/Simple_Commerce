package com.zerobase.cms.user.domain.repository;

import com.zerobase.cms.user.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller> findByEmail(String email);
}
