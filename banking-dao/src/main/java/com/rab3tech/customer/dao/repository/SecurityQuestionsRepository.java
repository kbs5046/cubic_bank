package com.rab3tech.customer.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rab3tech.dao.entity.SecurityQuestions;

/**
 * @author nagendra
 * Spring Data JPA repository
 *
 */
public interface SecurityQuestionsRepository extends JpaRepository<SecurityQuestions, Integer> {
	@Query("SELECT tt FROM SecurityQuestions tt where tt.status = :name") 
	List<SecurityQuestions> findActiveQuestions(@Param("name") String name);
}

