package com.rab3tech.customer.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.RequestTypeRepository;
import com.rab3tech.customer.dao.repository.CustomerRepository;
import com.rab3tech.customer.dao.repository.CustomerRequestRepository;
import com.rab3tech.customer.service.CustomerRequestService;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.Customer;
import com.rab3tech.dao.entity.CustomerRequest;
import com.rab3tech.dao.entity.RequestType;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.utils.Utils;
import com.rab3tech.vo.CustomerRequestVO;
import com.rab3tech.vo.EmailVO;

@Transactional
@Service
public class CustomerRequestServiceImpl implements CustomerRequestService {
	
	@Autowired
	private CustomerRequestRepository customerRequestRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private RequestTypeRepository requestTypeRepository;
	
	@Autowired
	private AccountStatusRepository accountStatusRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Value("${bank.from.email:nagen@gmail.com}")
	private String fromEmail;

	@Override
	public String submitRequest(String email) {
		CustomerRequest customerRequest = new CustomerRequest();
		Customer customer = customerRepository.findByEmail(email).get();
		customerRequest.setCustomerId(customer);
		//RequestType requestType = requestTypeRepository.findById(1).get();
		RequestType requestType = requestTypeRepository.findByName("Check").get();
		customerRequest.setRequestType(requestType);
		//AccountStatus accountStatus = accountStatusRepository.findById(1).get();
		AccountStatus accountStatus = accountStatusRepository.findByName("PENDING").get();
		customerRequest.setAccountStatus(accountStatus);
		customerRequest.setDoe(new Timestamp(new Date().getTime()));
		customerRequest.setRefNumber("CR-" + Utils.genRandomAlphaNum());
		customerRequestRepository.save(customerRequest);
		
		
		System.out.println("Email sending .." + LocalDateTime.now());
		EmailVO emailVO = new EmailVO();
		emailVO.setTo(customer.getEmail());
		emailVO.setFrom(fromEmail);
		emailVO.setSubject("Regarding CheckBook Request");
		emailVO.setBody("Hello! your CheckBook Request is Submited successfully.");
		emailVO.setName(customer.getName());
		emailVO.setRefNo(customerRequest.getRefNumber());
		emailService.sendCheckBookRequestEmail(emailVO);
		System.out.println("Email done .." + LocalDateTime.now());
		
		return customerRequest.getRefNumber();
	}

	@Override
	public Optional<CustomerRequestVO> findIfRequestExist(String username) {
		Customer customer = customerRepository.findByEmail(username).get();
		Optional<CustomerRequest> optional = customerRequestRepository.findByCustomerId(customer);
		CustomerRequestVO customerRequestVO = null;
		if(optional.isPresent()) {
			customerRequestVO = new CustomerRequestVO();
			BeanUtils.copyProperties(optional.get(),customerRequestVO);
			return Optional.of(customerRequestVO);
		}
		return Optional.empty();
	}

}
