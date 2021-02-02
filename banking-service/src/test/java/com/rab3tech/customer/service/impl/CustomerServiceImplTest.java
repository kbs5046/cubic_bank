package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.AccountTypeRepository;
import com.rab3tech.admin.dao.repository.MagicCustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerQuestionsAnsRepository;
import com.rab3tech.customer.dao.repository.RoleRepository;
import com.rab3tech.customer.service.CustomerService;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.AccountType;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerQuestionAnswer;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.Role;
import com.rab3tech.service.exception.BankServiceException;
import com.rab3tech.vo.CustomerVO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplTest {
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private CustomerQuestionsAnsRepository customerQuestionsAnsRepository;
	
	@Mock
	private MagicCustomerRepository customerRepository;
	
	@Mock
	private CustomerService customerService;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private AccountTypeRepository accountTypeRepository;
	
	@Mock
	private AccountStatusRepository accountStatusRepository;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	private CustomerServiceImpl customerServiceImpl;
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
		public void testUdpadteCustomerProfileAccountTypeThrowsException() {
			CustomerVO customerVO = new CustomerVO();
			customerVO.setName("ramesh");
			customerVO.setJobTitle("Teacher");
			customerVO.setUserid("r@gmail.com");
			CustomerVO customerVO2 = new CustomerVO();
			customerVO2.setUserid("r@gmail.com");
			customerVO2.setId(1);
			customerVO2.setName("sitaula");
			customerVO2.setJobTitle("Programmer");
			when(customerService.findByUserid(customerVO.getUserid())).thenReturn(customerVO2);
			
			Customer customer = new Customer();
			customer.setId(1);
			customer.setName("sitaula");
			customer.setJobTitle("Programmer");
			Optional<Customer> oCustomer = Optional.of(customer);
			when(customerRepository.findByEmail(customerVO2.getUserid())).thenReturn(oCustomer);
			
			
			Optional<AccountType> optionalAcc = Optional.empty();
			when(accountTypeRepository.findByName(customerVO2.getAccType())).thenReturn(optionalAcc);
			
			thrown.expect(BankServiceException.class);
			thrown.expectMessage("Hey this " + customerVO2.getAccType() + " account type is not valid!");
			customerServiceImpl.udpadteCustomerProfile(customerVO);  
		}
	
	
	
	@Test
	public void testUdpadteCustomerProfileAccountStatusThrowsException() {
		CustomerVO customerVO = new CustomerVO();
		customerVO.setName("ramesh");
		customerVO.setJobTitle("Teacher");
		customerVO.setUserid("r@gmail.com");
		CustomerVO customerVO2 = new CustomerVO();
		customerVO2.setUserid("r@gmail.com");
		customerVO2.setId(1);
		customerVO2.setName("sitaula");
		customerVO2.setJobTitle("Programmer");
		when(customerService.findByUserid(customerVO.getUserid())).thenReturn(customerVO2);
		
		Customer customer = new Customer();
		customer.setId(1);
		customer.setName("sitaula");
		customer.setJobTitle("Programmer");
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(customerVO2.getUserid())).thenReturn(oCustomer);
		
		
		AccountType accountType = new AccountType(1, "code1", "saving","saving");
		Optional<AccountType> optionalAcc = Optional.of(accountType);
		when(accountTypeRepository.findByName(customerVO2.getAccType())).thenReturn(optionalAcc);
		
		Optional<AccountStatus> optionalAccS = Optional.empty();
		when(accountStatusRepository.findByName(customerVO2.getStatus())).thenReturn(optionalAccS);
		
		thrown.expect(BankServiceException.class);
		thrown.expectMessage("Hey this " + customerVO2.getStatus() + " account status is not valid!");
		customerServiceImpl.udpadteCustomerProfile(customerVO);  
	}
	
	@Test
	public void testUdpadteCustomerProfileAccountTypeStatusPresent() {
		CustomerVO customerVO = new CustomerVO();
		customerVO.setName("ramesh");
		customerVO.setJobTitle("Teacher");
		customerVO.setUserid("r@gmail.com");
		CustomerVO customerVO2 = new CustomerVO();
		customerVO2.setUserid("r@gmail.com");
		customerVO2.setId(1);
		customerVO2.setName("sitaula");
		customerVO2.setJobTitle("Programmer");
		when(customerService.findByUserid(customerVO.getUserid())).thenReturn(customerVO2);
		
		Customer customer = new Customer();
		customer.setId(1);
		customer.setName("sitaula");
		customer.setJobTitle("Programmer");
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(customerVO2.getUserid())).thenReturn(oCustomer);
		
		
		AccountType accountType = new AccountType(1, "code1", "saving","saving");
		Optional<AccountType> optionalAcc = Optional.of(accountType);
		when(accountTypeRepository.findByName(customerVO2.getAccType())).thenReturn(optionalAcc);
		
		AccountStatus accountStatus = new AccountStatus(2,"code2","approved","approved");
		Optional<AccountStatus> optionalAccS = Optional.of(accountStatus);
		when(accountStatusRepository.findByName(customerVO2.getStatus())).thenReturn(optionalAccS);
		
		customerServiceImpl.udpadteCustomerProfile(customerVO);
		
		assertEquals("ramesh", customer.getName());
		assertEquals("Teacher", customer.getJobTitle());
		verify(customerRepository, times(1)).save(customer);
	  
	}
	
	@Test
	public void testFindByUserid() {
		String username = "r@gmail.com";
		Customer customer = new Customer();
		customer.setEmail("s@gmail.com");
		customer.setName("Ramesh");
		customer.setAccType(new AccountType(1, "code1", "saving","saving"));
		customer.setStatus(new AccountStatus(2,"code2","approved","approved"));
		Login login = new Login();
		login.setLoginid("myNameIsRamesh");
		customer.setLogin(login);
		Optional<Customer> oCustomer = Optional.of(customer);
		when(customerRepository.findByEmail(username)).thenReturn(oCustomer);
		
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		CustomerQuestionAnswer customerQuestionAnswer1 = new CustomerQuestionAnswer();
		customerQuestionAnswer1.setQuestion("school?");
		customerQuestionAnswer1.setAnswer("VMSS");
		CustomerQuestionAnswer customerQuestionAnswer2 = new CustomerQuestionAnswer();
		customerQuestionAnswer2.setQuestion("job?");
		customerQuestionAnswer2.setAnswer("engineer");
		customerQuestionAnswers.add(customerQuestionAnswer1);
		customerQuestionAnswers.add(customerQuestionAnswer2);
		when(customerQuestionsAnsRepository.findQuestionAnswer(username)).thenReturn(customerQuestionAnswers);
		
		CustomerVO actual = customerServiceImpl.findByUserid(username);
		assertEquals("Ramesh", actual.getName());
		assertEquals("saving", actual.getAccType());
		assertEquals("myNameIsRamesh", actual.getUserid());
		assertEquals("school?", actual.getQuestion1());
		assertEquals("engineer", actual.getAnswer2());
		
	}
	
	@Test
	public void testCreateAccountAccountTypeNotPresent() {
		CustomerVO customerVO = new CustomerVO();
		customerVO.setName("Ramesh");
		customerVO.setEmail("r@gmail.com");
		customerVO.setToken("abc");
		customerVO.setAccType("saving");
		customerVO.setUserid("r@gmail.com");
		customerVO.setId(0);
		Customer pcustomer = new Customer();
		BeanUtils.copyProperties(customerVO, pcustomer);
		Role role = new Role(1, "Customer","Customer");
		Optional<Role> oRole = Optional.of(role);
		when(roleRepository.findById(3)).thenReturn(oRole);
		
		Optional<AccountType> oAccountType = Optional.empty();
		when(accountTypeRepository.findByName(customerVO.getAccType())).thenReturn(oAccountType);
		
		thrown.expect(BankServiceException.class);
		thrown.expectMessage("Hey this " + customerVO.getAccType() + " account type is not valid!");
		customerServiceImpl. createAccount(customerVO);
	}
	
	@Test
	public void testCreateAccountAccountTypePresent() {
		CustomerVO customerVO = new CustomerVO();
		customerVO.setName("Ramesh");
		customerVO.setEmail("r@gmail.com");
		customerVO.setToken("abc");
		customerVO.setAccType("saving");
		customerVO.setUserid("r@gmail.com");
		customerVO.setId(0);
		Customer pcustomer = new Customer();
		BeanUtils.copyProperties(customerVO, pcustomer);
		Role role = new Role(1, "Customer","Customer");
		Optional<Role> oRole = Optional.of(role);
		when(roleRepository.findById(3)).thenReturn(oRole);
		
		AccountType accountType = new AccountType(1, "code1", "saving","saving");
		Optional<AccountType> oAccountType = Optional.of(accountType);
		when(accountTypeRepository.findByName(customerVO.getAccType())).thenReturn(oAccountType);
		
		Customer dcustomer = new Customer();
		BeanUtils.copyProperties(customerVO, dcustomer);
		dcustomer.setAccType(accountType);
		dcustomer.setId(25);
		when(customerRepository.save(pcustomer)).thenReturn(dcustomer);
		
		
		CustomerVO actual =customerServiceImpl. createAccount(customerVO);
		assertEquals(25, actual.getId());
		assertEquals("saving",actual.getAccType());
		assertEquals("r@gmail.com", dcustomer.getEmail());
	}

	@Test
	public void testFindAllCustomers() {
		Customer customer1 = new Customer();
		customer1.setEmail("r@gmail.com");
		customer1.setName("Ramesh");
		Customer customer2 = new Customer();
		customer2.setFather("rishi");
		customer2.setAddress("dallas");
		List<Customer> customers = new ArrayList<>();
		customers.add(customer1);
		customers.add(customer2);
		when(customerRepository.findAll()).thenReturn(customers);
		
		List<CustomerVO> actual = customerServiceImpl.findAllCustomers();
		assertEquals("r@gmail.com", actual.get(0).getEmail());
		assertEquals("Ramesh", actual.get(0).getName());
		assertEquals("rishi", actual.get(1).getFather());
		assertEquals("dallas", actual.get(1).getAddress());
	}
}
