package com.zerobase.cms.zerobasecms.domain.repository;

import com.zerobase.cms.zerobasecms.domain.model.CustomerBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory,Long> {

    Optional<CustomerBalanceHistory> findFirstByCustomer_idOrderByIdDesc(@RequestParam("customer_id") Long customerId);
}
