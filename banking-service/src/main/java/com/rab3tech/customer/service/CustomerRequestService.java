package com.rab3tech.customer.service;

import java.util.Optional;

import com.rab3tech.vo.CustomerRequestVO;

public interface CustomerRequestService {

	String submitRequest(String email);

	Optional<CustomerRequestVO> findIfRequestExist(String username);

}
