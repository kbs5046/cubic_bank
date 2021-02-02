package com.rab3tech.customer.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.rab3tech.utils.NullPropertyUtils;
import com.rab3tech.utils.PasswordGenerator;
import com.rab3tech.vo.CustomerVO;

@Service
@Transactional
public class CustomerServiceImpl implements  CustomerService{
	
	@Autowired
 	private CustomerQuestionsAnsRepository customerQuestionsAnsRepository;
	
	@Autowired
	private MagicCustomerRepository customerRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private AccountTypeRepository accountTypeRepository;
	
	@Autowired	
	private AccountStatusRepository accountStatusRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public void udpadteCustomerProfile(CustomerVO customerVO) {
		CustomerVO customerVO2 = customerService.findByUserid(customerVO.getUserid());
		NullPropertyUtils.copyNonNullProperties(customerVO, customerVO2);
		Customer customer = customerRepository.findByEmail(customerVO2.getUserid()).get();
		BeanUtils.copyProperties(customerVO2, customer);
		Optional<AccountType> optionalAcc = accountTypeRepository.findByName(customerVO2.getAccType());
		if (optionalAcc.isPresent()) {
			customer.setAccType(optionalAcc.get());
		} else {
			throw new BankServiceException("Hey this " + customerVO2.getAccType() + " account type is not valid!");
		}
		Optional<AccountStatus> optionalAccS = accountStatusRepository.findByName(customerVO2.getStatus());
		if (optionalAccS.isPresent()) {
			customer.setStatus(optionalAccS.get());
		} else {
			throw new BankServiceException("Hey this " + customerVO2.getStatus() + " account status is not valid!");
		}
		customer.setDoe(new Timestamp(new Date().getTime()));
		customer.setDom(new Timestamp(new Date().getTime()));
		customerRepository.save(customer);
	}
	
	@Override
	public CustomerVO findByUserid(String username) {
		Customer customer = customerRepository.findByEmail(username).get();
		List<CustomerQuestionAnswer> customerQuestionAnswers=customerQuestionsAnsRepository.findQuestionAnswer(username);
		CustomerVO customerVO = new CustomerVO();
		BeanUtils.copyProperties(customer, customerVO);
		customerVO.setAccType(customer.getAccType().getName());
		customerVO.setStatus(customer.getStatus().getName());
		customerVO.setUserid(customer.getLogin().getLoginid());
		customerVO.setQuestion1(customerQuestionAnswers.get(0).getQuestion());
		customerVO.setAnswer1(customerQuestionAnswers.get(0).getAnswer());
		customerVO.setQuestion2(customerQuestionAnswers.get(1).getQuestion());
		customerVO.setAnswer2(customerQuestionAnswers.get(1).getAnswer());
		return customerVO;
	}
	
	@Override
	public CustomerVO createAccount(CustomerVO customerVO) {
		Customer pcustomer = new Customer();
		BeanUtils.copyProperties(customerVO, pcustomer);
		Login login = new Login();
		login.setNoOfAttempt(3);
		login.setLoginid(customerVO.getEmail());
		login.setName(customerVO.getName());
		String genPassword=PasswordGenerator.generateRandomPassword(8);
		customerVO.setPassword(genPassword);
		login.setPassword(bCryptPasswordEncoder.encode(genPassword));
		login.setToken(customerVO.getToken());
		login.setLocked("no");
		
		Role entity=roleRepository.findById(3).get();
		Set<Role> roles=new HashSet<>();
		roles.add(entity);
		//setting roles inside login
		login.setRoles(roles);
		//setting login inside
		pcustomer.setLogin(login);
		//setting acount type and status
		Optional<AccountType> optionalAcc = accountTypeRepository.findByName(customerVO.getAccType());
		if (optionalAcc.isPresent()) {
			pcustomer.setAccType(optionalAcc.get());
		} else {
			throw new BankServiceException("Hey this " + customerVO.getAccType() + " account type is not valid!");
		}

		AccountStatus accountStatus = new AccountStatus();
		accountStatus.setId(1);
		pcustomer.setStatus(accountStatus);
		
		Customer dcustomer=customerRepository.save(pcustomer);
		customerVO.setId(dcustomer.getId());
		customerVO.setUserid(customerVO.getUserid());
		return customerVO;
	}

	@Override
	public List<CustomerVO> findAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		List<CustomerVO> customerVOs = new ArrayList<>();
		for(Customer customer: customers)
		{
			CustomerVO customerVO = new CustomerVO();
			BeanUtils.copyProperties(customer, customerVO);
			customerVOs.add(customerVO);
		}
		return customerVOs;
	}
	
	

}
