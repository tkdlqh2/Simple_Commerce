package com.zerobase.cms.user.domain.repository;

import com.zerobase.cms.user.domain.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller,Long> {
    Optional<Seller> findByEmail(String email);
}
