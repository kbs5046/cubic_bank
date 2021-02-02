package com.rab3tech.admin.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rab3tech.dao.entity.RequestType;

public interface RequestTypeRepository extends JpaRepository<RequestType, Integer> {

	List<RequestType> findByStatus(int i);

	Optional<RequestType> findByName(String string);

}
