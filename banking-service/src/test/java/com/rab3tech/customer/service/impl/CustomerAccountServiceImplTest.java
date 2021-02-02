package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.customer.dao.repository.CreateAccountRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountRepository;
import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.AccountType;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.utils.AccountStatusEnum;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerVO;


@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountServiceImplTest {
	@Mock
	private CustomerAccountRepository customerAccountRepository;
	
	@Mock
	private CreateAccountRepository createAccountRepository;
	
	@Mock
	private LoginRepository loginRepository;
	
	@Mock
	private AccountStatusRepository accountStatusRepository;
	
	@InjectMocks
	private CustomerAccountServiceImpl customerAccountServiceImpl;
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testfindPendingAccount() {
		Customer customer1 = new Customer();
		customer1.setStatus(new AccountStatus(1,"code1", "pending","pending"));
		customer1.setId(1);
		customer1.setEmail("r@gmail.com");
		Login login1 = new Login();
		login1.setLoginid("r@gmail.com");
		customer1.setLogin(login1);
		Customer customer2 = new Customer();
		customer2.setAccType(new AccountType(1, "code1", "saving","saving"));
		customer2.setId(2);
		customer2.setEmail("s@gmail.com");
		Login login2 = new Login();
		login2.setLoginid("s@gmail.com");
		customer2.setLogin(login2);
		List<Customer> customerList =new ArrayList<>();
		customerList.add(customer1);
		customerList.add(customer2);
		when(customerAccountRepository.findPendingAcounts(AccountStatusEnum.PENDING.name())).thenReturn(customerList);
		
		List<CustomerVO> actual = customerAccountServiceImpl.findPendingAccount();
		assertEquals("r@gmail.com", actual.get(0).getEmail());
		assertEquals("s@gmail.com", actual.get(1).getEmail());
		assertEquals(1, actual.get(0).getId());
		assertEquals(2, actual.get(1).getId());
		assertEquals("r@gmail.com", actual.get(0).getUserid());
		assertEquals("s@gmail.com", actual.get(1).getUserid());
		verify(customerAccountRepository, times(1)).findPendingAcounts(AccountStatusEnum.PENDING.name());
	    verifyNoMoreInteractions(customerAccountRepository);
	}

	
	@Test
	public void testcreateAccount() {
		CustomerAccountInfoVO customerAccountInfoVo = new CustomerAccountInfoVO();
		customerAccountInfoVo.setCustomerId("r@gmail.com");
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		BeanUtils.copyProperties(customerAccountInfoVo,customerAccountInfo);
		
		Login login = new Login();
		login.setLoginid("r@gmail.com");
		Optional<Login> oLogin = Optional.of(login);
		when(loginRepository.findByLoginid(customerAccountInfo.getCustomerId())).thenReturn(oLogin);
		
		Customer customer = new Customer();
		customer.setLogin(login);
		customer.setEmail("r@gmail.com");
		Optional<Customer> optional = Optional.of(customer);
		when(customerAccountRepository.findByLogin(oLogin.get())).thenReturn(optional);
		
		AccountStatus accountStatus = new AccountStatus(1,"code1", "approved","approved");
		Optional<AccountStatus> oAccountStatus  = Optional.of(accountStatus);
		when(accountStatusRepository.findByName("APPROVED")).thenReturn(oAccountStatus);
		
		customerAccountServiceImpl.createAccount(customerAccountInfoVo);
		
		assertEquals("approved", customer.getStatus().getName());
		verify(createAccountRepository, times(1)).save(customerAccountInfo);
        verifyNoMoreInteractions(createAccountRepository);
        verify(loginRepository, times(1)).findByLoginid(customerAccountInfo.getCustomerId());
        verifyNoMoreInteractions(loginRepository);
        verify(customerAccountRepository, times(1)).findByLogin(oLogin.get());
        verifyNoMoreInteractions(customerAccountRepository);
        verify(accountStatusRepository, times(1)).findByName("APPROVED");
        verifyNoMoreInteractions(accountStatusRepository);	
		
	}
	
	/*
	 public CustomerVO findByUsername(String username) {
		Login login = loginRepository.findByLoginid(username).get();
		Customer customer = customerAccountRepository.findByLogin(login).get();
		CustomerVO customerVO = new CustomerVO();
		BeanUtils.copyProperties(customer, customerVO);
		customerVO.setUserid(login.getLoginid());
		customerVO.setAccType(customer.getAccType().getName());
		customerVO.setStatus(customer.getStatus().getName());
		return customerVO;
	}
	 */
	
	@Test
	public void testfindByUsername() {
		String uername= "r@gmail.com";
		Login login = new Login();
		login.setLoginid(uername);
		login.setName("ram");
		Optional<Login> oLogin = Optional.of(login);
		when(loginRepository.findByLoginid(uername)).thenReturn(oLogin);
		
		Customer customer = new Customer();
		customer.setLogin(login);
		customer.setAge(50);
		customer.setAccType(new AccountType(1, "code1", "saving","saving"));
		customer.setStatus(new AccountStatus(1,"code1", "approved","approved"));
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerAccountRepository.findByLogin(oLogin.get())).thenReturn(oCustomer);
		
		CustomerVO actual = customerAccountServiceImpl.findByUsername(uername);
		assertEquals("saving", actual.getAccType());
		assertEquals("approved", actual.getStatus());
		assertEquals("r@gmail.com", actual.getUserid());
		assertEquals(50, actual.getAge());
	}
	

}
