package com.rab3tech.customer.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerRequest;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Integer> {

	@Query("SELECT tt FROM CustomerRequest tt where tt.customerId = :customerId") 
	Optional<CustomerRequest> findByCustomerId(@Param("customerId")Customer customer);

}
