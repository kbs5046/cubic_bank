package com.rab3tech.customer.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.Login;

public interface CustomerAccountRepository extends JpaRepository<Customer, Integer> {

	public Optional<Customer> findByLogin(Login login);
	
	@Query("SELECT tt FROM Customer tt where tt.status.name = :name") 
	List<Customer> findPendingAcounts(@Param("name") String name);


}
