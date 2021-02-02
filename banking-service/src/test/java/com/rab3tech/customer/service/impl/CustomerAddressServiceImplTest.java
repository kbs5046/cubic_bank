package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import com.rab3tech.customer.dao.repository.CustomerAddressRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAddress;
import com.rab3tech.vo.CustomerAddressVO;
import com.rab3tech.vo.CustomerVO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAddressServiceImplTest {
	
	@Mock
	private CustomerAddressRepository customerAddressRepository;
	
	@Mock
	private CustomerRepository customerRepository;
	
	@InjectMocks
	private CustomerAddressServiceImpl customerAddressServiceImpl;
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this); //Initializing mocking for each test cases
	}

	@Test
	public void testSaveAddress() {
		CustomerAddressVO customerAddressVO = new CustomerAddressVO();;
		customerAddressVO.setAddressLine1("block dr.");
		customerAddressVO.setCity("irving");
		CustomerAddress customerAddress = new CustomerAddress();
		BeanUtils.copyProperties(customerAddressVO, customerAddress);
		Customer customer = new Customer();
		customer.setEmail("r@gmail.com");
		customer.setId(2);
		Optional<Customer> oCustomer = Optional.of(customer);
		CustomerAddress address = new CustomerAddress();
		BeanUtils.copyProperties(customerAddressVO, address);
		address.setAddressId(1);
		address.setCustomerID(oCustomer.get());
		when(customerRepository.findByEmail("r@gmail.com")).thenReturn(oCustomer);
		//when(customerAddressRepository.save(customerAddress)).thenReturn(address);
		
		customerAddressServiceImpl.saveAddress(customerAddressVO, "r@gmail.com");
		assertEquals("r@gmail.com", address.getCustomerID().getEmail());
		assertEquals(1, address.getAddressId());
		assertEquals("block dr.", address.getAddressLine1());
		
		verify(customerRepository,times(1)).findByEmail("r@gmail.com");
		verifyNoMoreInteractions(customerRepository);	
//		verify(customerAddressRepository,times(1)).save(customerAddress);
//		verifyNoMoreInteractions(customerAddressRepository);	
		
	}
	
	@Test
	public void testfindByCustomerIdWhenExist() {
		Customer customer = new Customer();
		customer.setEmail("r@gmail.com");
		customer.setId(1);
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail("r@gmail.com")).thenReturn(oCustomer);
		
		CustomerAddress customerAddress = new CustomerAddress();
		customerAddress.setAddressId(2);
		customerAddress.setCustomerID(oCustomer.get());
		Optional<CustomerAddress> optional = Optional.of(customerAddress);
		when(customerAddressRepository.findByCustomerID(oCustomer.get())).thenReturn(optional);
		
		CustomerAddressVO addressVO = new CustomerAddressVO();
		BeanUtils.copyProperties(optional.get(), addressVO);
		CustomerVO customerVO = new CustomerVO();
		BeanUtils.copyProperties(customer, customerVO);
		addressVO.setCustomerID(customerVO);
		
		Optional<CustomerAddressVO> result = customerAddressServiceImpl.findByCustomerId("r@gmail.com");
		
		assertEquals("r@gmail.com", result.get().getCustomerID().getEmail());
		assertEquals(2, result.get().getAddressId());
		verify(customerRepository,times(1)).findByEmail("r@gmail.com");
		verifyNoMoreInteractions(customerRepository);
		verify(customerAddressRepository,times(1)).findByCustomerID(oCustomer.get());
		verifyNoMoreInteractions(customerAddressRepository);	
		
	}
	
	
	@Test
	public void testfindByCustomerIdWhenNotExist() {
		Customer customer = new Customer();
		customer.setEmail("r@gmail.com");
		customer.setId(1);
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail("r@gmail.com")).thenReturn(oCustomer);
		
		Optional<CustomerAddress> optional = Optional.empty();
		when(customerAddressRepository.findByCustomerID(oCustomer.get())).thenReturn(optional);	
		Optional<CustomerAddressVO> result = customerAddressServiceImpl.findByCustomerId("r@gmail.com");
		
		assertEquals(false, result.isPresent());
		verify(customerRepository,times(1)).findByEmail("r@gmail.com");
		verifyNoMoreInteractions(customerRepository);
		verify(customerAddressRepository,times(1)).findByCustomerID(oCustomer.get());
		verifyNoMoreInteractions(customerAddressRepository);
	}
}
