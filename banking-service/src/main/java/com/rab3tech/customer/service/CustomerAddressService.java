package com.rab3tech.customer.service;

import java.util.Optional;

import com.rab3tech.vo.CustomerAddressVO;

public interface CustomerAddressService {

	Optional<CustomerAddressVO> findByCustomerId(String usernme);

	void saveAddress(CustomerAddressVO customerAddressVO, String username);

}
