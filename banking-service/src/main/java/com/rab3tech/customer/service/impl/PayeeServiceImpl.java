package com.rab3tech.customer.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.customer.dao.repository.CreateAccountRepository;
import com.rab3tech.customer.dao.repository.PayeeInfoRepository;
import com.rab3tech.customer.service.PayeeService;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.vo.PayeeInfoVO;

@Service
@Transactional
public class PayeeServiceImpl implements PayeeService {
	
	@Autowired
	private CreateAccountRepository createAccountRepository;
	
	@Autowired
	private PayeeInfoRepository payeeInfoRepository;
	
	@Override
	public String updatePayee(PayeeInfoVO payeeInfoVO) {
		Optional<CustomerAccountInfo> cusOptional = createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId());
		if(cusOptional.isPresent()) {
			if(!cusOptional.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your account is not active.. Please contact bank first to activate your account";
			}
			if(!cusOptional.get().getAccountType().equalsIgnoreCase("SAVING")) {
				return "You do not have a saving account";
			}
			if(cusOptional.get().getAccountNumber().equalsIgnoreCase(payeeInfoVO.getPayeeAccountNo())) {
				return "You can not add yourself as a payee..";
			}
		}
		else {
			return "You do not have a valid account number";
		}
		
		Optional<CustomerAccountInfo> optional = createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo());
		if(optional.isPresent()) {
			if(!optional.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your payee's account is not active..";
			}
			if(!optional.get().getAccountType().equalsIgnoreCase("SAVING")) {
				return "Your payee do not have a saving account";
			}
			
		}
		else {
			return "Account number you entered is not a valid account number.";
		}
		
		Optional<PayeeInfo> optional2 = payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName());
		if(optional2.get().getPayeeNickName().equalsIgnoreCase(payeeInfoVO.getPayeeNickName())) {
			return "Payee with the same same nickname already exists.. please try different name.";
		}
		
		PayeeInfo optional3 = payeeInfoRepository.findById(payeeInfoVO.getId()).get();
		//PayeeInfo payeeInfo = new PayeeInfo();
		BeanUtils.copyProperties(payeeInfoVO, optional3);
		//payeeInfo.setDoe(optional3.getDoe());
		optional3.setDom(new Timestamp(new Date().getTime()));
		payeeInfoRepository.save(optional3);
		return "Payee information have been updated..";
	}
	
	@Override
	public PayeeInfoVO getPayee(int id) {
		PayeeInfo payeeInfo = payeeInfoRepository.findById(id).get();
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		BeanUtils.copyProperties(payeeInfo, payeeInfoVO);
		return payeeInfoVO;
	}
	
	@Override
	public void removePayee(int id) {
		PayeeInfo payeeInfo = payeeInfoRepository.findById(id).get();
		payeeInfoRepository.delete(payeeInfo);
	}
	
	@Override
	public List<PayeeInfoVO> getPayeeList(String username) {
		List<PayeeInfo> payeeInfos = payeeInfoRepository.findByCustomerId(username);
		List<PayeeInfoVO> payeeInfoVOs = new ArrayList<PayeeInfoVO>();
		for(PayeeInfo payeeInfo: payeeInfos) {
			PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
			BeanUtils.copyProperties(payeeInfo, payeeInfoVO);
			payeeInfoVOs.add(payeeInfoVO);
		}
		return payeeInfoVOs;
	}
	
	@Override
	public String savePayee(PayeeInfoVO payeeInfoVO) {
		Optional<CustomerAccountInfo> cusOptional = createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId());
		if(cusOptional.isPresent()) {
			if(!cusOptional.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your account is not active.. Please contact bank first to activate your account";
			}
			if(!cusOptional.get().getAccountType().equalsIgnoreCase("SAVING")) {
				return "You do not have a saving account";
			}
			if(cusOptional.get().getAccountNumber().equalsIgnoreCase(payeeInfoVO.getPayeeAccountNo())) {
				return "You can not add yourself as a payee..";
			}
		}
		else {
			return "You do not have a valid account number";
		}
		
		Optional<CustomerAccountInfo> optional = createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo());
		if(optional.isPresent()) {
			if(!optional.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your payee's account is not active..";
			}
			if(!optional.get().getAccountType().equalsIgnoreCase("SAVING")) {
				return "Your payee do not have a saving account";
			}
			
		}
		else {
			return "Account number you entered is not a valid account number.";
		}
		
		Optional<PayeeInfo> optional2 = payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName());
		if(optional2.isPresent()) {
			return "Payee with the same same nickname already exists.. please try different name.";
		}
		
		Optional<PayeeInfo> optional3 = payeeInfoRepository.findByCustomerIdAndPayeeAccountNo(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeAccountNo());
		if(optional3.isPresent()) {
			return "Payee with same account no. already exist..";
		}
		
		PayeeInfo payeeInfo = new PayeeInfo();
		BeanUtils.copyProperties(payeeInfoVO, payeeInfo);
		payeeInfoRepository.save(payeeInfo);
		payeeInfo.setDoe(new Timestamp(new Date().getTime()));
		payeeInfo.setDom(new Timestamp(new Date().getTime()));
		return "New payee have been added to your account.";
	}

}
