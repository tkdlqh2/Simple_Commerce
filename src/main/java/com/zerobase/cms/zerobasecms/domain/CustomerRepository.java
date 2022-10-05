package com.zerobase.cms.zerobasecms.domain;

import com.zerobase.cms.zerobasecms.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
