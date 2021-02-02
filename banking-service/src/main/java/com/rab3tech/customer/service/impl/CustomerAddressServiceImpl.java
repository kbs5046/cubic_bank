package com.rab3tech.customer.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.customer.dao.repository.CustomerAddressRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.service.CustomerAddressService;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAddress;
import com.rab3tech.vo.CustomerAddressVO;
import com.rab3tech.vo.CustomerVO;

@Transactional
@Service
public class CustomerAddressServiceImpl implements CustomerAddressService {
	
	@Autowired
	private CustomerAddressRepository customerAddressRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public void saveAddress(CustomerAddressVO customerAddressVO,String username) {
		CustomerAddress customerAddress = new CustomerAddress();
		BeanUtils.copyProperties(customerAddressVO, customerAddress);
		Customer customer = customerRepository.findByEmail(username).get();
		customerAddress.setCustomerID(customer);
		customerAddressRepository.save(customerAddress);
	}
	
	@Override
	public Optional<CustomerAddressVO> findByCustomerId(String username) {
		Customer customer = customerRepository.findByEmail(username).get();
		Optional<CustomerAddress> optional = customerAddressRepository.findByCustomerID(customer);
		CustomerAddressVO addressVO = null;
		if(optional.isPresent()) {
			addressVO = new CustomerAddressVO();
			BeanUtils.copyProperties(optional.get(), addressVO);
			CustomerVO customerVO = new CustomerVO();
			BeanUtils.copyProperties(customer, customerVO);
			addressVO.setCustomerID(customerVO);
			return Optional.of(addressVO);
		}
		return Optional.empty();
	}

}
