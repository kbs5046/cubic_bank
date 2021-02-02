package com.rab3tech.customer.service;

import java.util.List;

import com.rab3tech.vo.CustomerVO;

public interface CustomerService {

	CustomerVO createAccount(CustomerVO customerVO);

	CustomerVO findByUserid(String username);

	void udpadteCustomerProfile(CustomerVO customerVO);

	List<CustomerVO> findAllCustomers();

}
