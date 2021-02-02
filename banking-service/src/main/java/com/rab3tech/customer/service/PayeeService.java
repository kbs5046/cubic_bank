package com.rab3tech.customer.service;

import java.util.List;

import com.rab3tech.vo.PayeeInfoVO;

public interface PayeeService {

	String savePayee(PayeeInfoVO payeeInfoVO);

	List<PayeeInfoVO> getPayeeList(String username);

	void removePayee(int id);

	PayeeInfoVO getPayee(int id);

	String updatePayee(PayeeInfoVO payeeInfoVO);

}
