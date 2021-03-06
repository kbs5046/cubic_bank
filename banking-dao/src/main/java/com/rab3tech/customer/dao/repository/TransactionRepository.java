package com.rab3tech.customer.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rab3tech.dao.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
	@Query("select t from Transaction t where t.debitAccountNo = :accountNumber or t.payeeId.payeeAccountNo = :accountNumber order by t.transactionDate desc")
	List<Transaction> findAllTransactions(@Param("accountNumber") String accountNumber);
	
}
