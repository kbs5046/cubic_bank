package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.RequestTypeRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerRequestRepository;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerRequest;
import com.rab3tech.dao.entity.RequestType;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.vo.CustomerRequestVO;
import com.rab3tech.vo.EmailVO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerRequestServiceImplTest {
	@Mock
	private CustomerRequestRepository customerRequestRepository;
	
	@Mock
	private CustomerRepository customerRepository;
	
	@Mock
	private RequestTypeRepository requestTypeRepository;
	
	@Mock
	private AccountStatusRepository accountStatusRepository;
	
	@Mock
	private EmailService emailService;
	
	@InjectMocks
	private CustomerRequestServiceImpl customerRequestServiceImpl;
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testfindIfRequestExistWhenExist() {
		String username = "r@gmail.com";
		Customer customer  = new Customer();
		customer.setId(1);
		customer.setEmail("r@gmail.com");
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(username)).thenReturn(oCustomer);
		
		CustomerRequest customerRequest = new CustomerRequest();
		customerRequest.setRequestId(5);
		customerRequest.setRefNumber("abc");
		customerRequest.setCustomerId(customer);
		Optional<CustomerRequest> optional = Optional.of(customerRequest);
		when(customerRequestRepository.findByCustomerId(customer)).thenReturn(optional);
		
		Optional<CustomerRequestVO> actual = customerRequestServiceImpl.findIfRequestExist(username);
		assertEquals("abc", actual.get().getRefNumber());
		assertEquals(5, actual.get().getRequestId());
		
	}
	
	@Test
	public void testfindIfRequestExistWhenNotExist() {
		String username = "r@gmail.com";
		Customer customer  = new Customer();
		customer.setId(1);
		customer.setEmail("r@gmail.com");
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(username)).thenReturn(oCustomer);
		
		Optional<CustomerRequest> optional = Optional.empty();
		when(customerRequestRepository.findByCustomerId(customer)).thenReturn(optional);
		
		Optional<CustomerRequestVO> actual = customerRequestServiceImpl.findIfRequestExist(username);
		assertFalse(actual.isPresent());
		
	}
	
	@Test
	public void testSubmitRequest() {
		CustomerRequest customerRequest = new CustomerRequest();
		String email = "r@gmail.com";
		Customer customer  = new Customer();
		customer.setId(1);
		customer.setEmail("r@gmail.com");
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(email)).thenReturn(oCustomer);
		
		RequestType requestType = new RequestType(1, "code1", "loan", "loan", 1);
		Optional<RequestType> oRequestType = Optional.of(requestType);
		when(requestTypeRepository.findByName("Check")).thenReturn(oRequestType);
		
		AccountStatus accountStatus = new AccountStatus(2,"code2","approved","approved");
		Optional<AccountStatus> oAccountStatus = Optional.of(accountStatus);
		when(accountStatusRepository.findByName("PENDING")).thenReturn(oAccountStatus);
		customerRequest.setCustomerId(customer);
		customerRequest.setAccountStatus(accountStatus);
		customerRequest.setRequestType(requestType);
		customerRequest.setRefNumber("123$");

		String actual = customerRequestServiceImpl.submitRequest(email);		
		assertFalse(actual.isEmpty());
		verify(customerRequestRepository,times(1)).save(customerRequest);
		verifyNoMoreInteractions(customerRequestRepository);
		verify(emailService, times(1)).sendCheckBookRequestEmail(new EmailVO(customer.getEmail(), "javahunk100@gmail.com", null,
				"Hello! your CheckBook Request is Submited successfully.", null));
 	    verifyNoMoreInteractions(emailService);
	}


}
