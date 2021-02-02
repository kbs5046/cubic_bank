package com.rab3tech.customer.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.customer.dao.repository.CreateAccountRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountRepository;
import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.customer.service.CustomerAccountService;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.utils.AccountStatusEnum;
import com.rab3tech.vo.CustomerAccountInfoVO;
import com.rab3tech.vo.CustomerVO;

@Service
@Transactional
public class CustomerAccountServiceImpl implements CustomerAccountService {
	@Autowired
	private CustomerAccountRepository customerAccountRepository;
	
	@Autowired
	private CreateAccountRepository createAccountRepository;
	
	@Autowired
	private LoginRepository loginRepository;
	
	@Autowired
	private AccountStatusRepository accountStatusRepository;

	@Override
	public List<CustomerVO> findPendingAccount() {
		List<Customer> customerList = customerAccountRepository.findPendingAcounts(AccountStatusEnum.PENDING.name());		
		return convertEntityIntoVO(customerList);
	}

	@Override
	public void createAccount(CustomerAccountInfoVO customerAccountInfoVo) {		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		BeanUtils.copyProperties(customerAccountInfoVo,customerAccountInfo);
		customerAccountInfo.setAccountNumber(generateAccountNo());
		customerAccountInfo.setAvBalance(1000);
		customerAccountInfo.setCurrency("USD");
		customerAccountInfo.setStatusAsOf(new Timestamp(new Date().getTime()));
		customerAccountInfo.setTavBalance(customerAccountInfo.getAvBalance());
		createAccountRepository.save(customerAccountInfo);
		Login login = loginRepository.findByLoginid(customerAccountInfo.getCustomerId()).get();
		Customer customer = customerAccountRepository.findByLogin(login).get();
		String status = "APPROVED";
		AccountStatus accountStatus=accountStatusRepository.findByName(status).get();
		//Updating account status
		customer.setStatus(accountStatus);
	}

	@Override
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
	
	private List<CustomerVO> convertEntityIntoVO(List<Customer> customerList) {
		List<CustomerVO> customerVOList = new ArrayList<>();
		for (Customer customer : customerList) {
			Login login = customer.getLogin();
			CustomerVO customerVO = new CustomerVO();
			customerVO.setUserid(login.getLoginid());
			BeanUtils.copyProperties(customer, customerVO, new String[] { "accType", "status" });
			customerVO.setAccType(customer.getAccType()!=null?customer.getAccType().getName():null);
			customerVO.setStatus(customer.getStatus()!=null?customer.getStatus().getName():null);
			customerVOList.add(customerVO);
		}
		return customerVOList;
	}
	
	private static String generateAccountNo() {
		Random rand = new Random();		
		String account = "CB";
		for(int i=0;i<14;i++) {
			int num = rand.nextInt(10);
			account +=Integer.toString(num);
		}
		return account;	
	}

}
