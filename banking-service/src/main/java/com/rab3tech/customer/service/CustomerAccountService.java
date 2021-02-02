package com.rab3tech.customer.service;

import java.util.List;

import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerVO;

public interface CustomerAccountService {
	List<CustomerVO> findPendingAccount();
	void createAccount(CustomerAccountInfoVO customerAccountInfoVo);
	CustomerVO findByUsername(String userid);

}
