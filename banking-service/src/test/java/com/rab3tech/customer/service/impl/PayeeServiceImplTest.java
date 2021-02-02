package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.rab3tech.customer.dao.repository.CreateAccountRepository;
import com.rab3tech.customer.dao.repository.PayeeInfoRepository;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.vo.PayeeInfoVO;

@RunWith(MockitoJUnitRunner.class)
public class PayeeServiceImplTest {
	
	@Mock
	private CreateAccountRepository createAccountRepository;
	
	@Mock
	private PayeeInfoRepository payeeInfoRepository;
	
	@InjectMocks
	private PayeeServiceImpl payeeServiceImpl;
	
	@Before
	public void init(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetPayeeList() {
		String username = "r@gmail.com";
		PayeeInfo payeeInfo1 = new PayeeInfo();
		payeeInfo1.setPayeeAccountNo("456");
		payeeInfo1.setCustomerId("r@gmail.com");	
		PayeeInfo payeeInfo2 = new PayeeInfo();
		payeeInfo2.setPayeeNickName("Sid");
		payeeInfo2.setCustomerId("r@gmail.com");
		List<PayeeInfo> payeeInfos = new ArrayList<>();
		payeeInfos.add(payeeInfo1);
		payeeInfos.add(payeeInfo2);
		when(payeeInfoRepository.findByCustomerId(username)).thenReturn(payeeInfos);		
		
		List<PayeeInfoVO> actual = payeeServiceImpl.getPayeeList(username);
		assertEquals("456", actual.get(0).getPayeeAccountNo());
		assertEquals("r@gmail.com", actual.get(0).getCustomerId());
		assertEquals("Sid", actual.get(1).getPayeeNickName());
		assertEquals("r@gmail.com", actual.get(1).getCustomerId());
		
	}
	
	@Test
	public void testRemovePayee() {
		int id = 1;
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid");
		payeeInfo.setRemarks("Loan");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findById(id)).thenReturn(oPayeeInfo);
		
		payeeServiceImpl.removePayee(id);
		verify(payeeInfoRepository, times(1)).delete(oPayeeInfo.get());
	
	}
	
	@Test
	public void testGetPayee() {
		int id = 1;
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid");
		payeeInfo.setRemarks("Loan");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findById(id)).thenReturn(oPayeeInfo);
		
		PayeeInfoVO actual = payeeServiceImpl.getPayee(id);
		assertEquals("456", actual.getPayeeAccountNo());
		assertEquals("Sid", actual.getPayeeNickName());
		assertEquals("Loan", actual.getRemarks());
		
	}
	
	@Test
	public void testSavePayeeWhenCustomerAccountNotActive() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("NotActive");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Your account is not active.. Please contact bank first to activate your account", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenCustomerAccountNotSaving() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("NotSaving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("You do not have a saving account", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenCustomerAccountEqualsPayee() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("123");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("You can not add yourself as a payee..", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenCustomerAccountNotPresent() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("123");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		Optional<CustomerAccountInfo> cusAccount = Optional.empty();
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("You do not have a valid account number", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenPayeeAccountNotActive() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("NotActive");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Your payee's account is not active..", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenPayeeAccountNotSaving() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("NotSaving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Your payee do not have a saving account", actual);
		
	}
	
	@Test
	public void testSavePayeeWhenPayeeAccountNotPresent() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		Optional<CustomerAccountInfo> payAccount = Optional.empty();
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Account number you entered is not a valid account number.", actual);
		
	}
	
	
	@Test
	public void testSavePayeeWhenPayeeWithSameNicknameExist() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		payeeInfoVO.setPayeeNickName("Sid");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName())).thenReturn(oPayeeInfo);
		
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Payee with the same same nickname already exists.. please try different name.", actual);
	}
	
	@Test
	public void testSavePayeeWhenPayeeWithSameAccountNoExist() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		payeeInfoVO.setPayeeNickName("Sid");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		Optional<PayeeInfo> oPayeeInfo = Optional.empty();
		when(payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName())).thenReturn(oPayeeInfo);
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo1 = Optional.of(payeeInfo);
		when(payeeInfoRepository.findByCustomerIdAndPayeeAccountNo(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeAccountNo())).thenReturn(oPayeeInfo1);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("Payee with same account no. already exist..", actual);
	}
	
	@Test
	public void testSavePayeeWhenEverythingOkay() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		payeeInfoVO.setPayeeNickName("Sid");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		Optional<PayeeInfo> oPayeeInfo = Optional.empty();
		when(payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName())).thenReturn(oPayeeInfo);
		
		Optional<PayeeInfo> oPayeeInfo1 = Optional.empty();
		when(payeeInfoRepository.findByCustomerIdAndPayeeAccountNo(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeAccountNo())).thenReturn(oPayeeInfo1);
		
		String actual = payeeServiceImpl.savePayee(payeeInfoVO);
		assertEquals("New payee have been added to your account.", actual);
		verify(payeeInfoRepository, times(1)).save(any(PayeeInfo.class));
	}
	
	@Test
	public void testUpdatePayeeWhenCustomerAccountNotActive() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("NotActive");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Your account is not active.. Please contact bank first to activate your account", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenCustomerAccountNotSaving() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("NotSaving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("You do not have a saving account", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenCustomerAccountEqualsPayee() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("123");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("You can not add yourself as a payee..", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenCustomerAccountNotPresent() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("123");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		Optional<CustomerAccountInfo> cusAccount = Optional.empty();
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("You do not have a valid account number", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenPayeeAccountNotActive() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("NotActive");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Your payee's account is not active..", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenPayeeAccountNotSaving() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("s@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("NotSaving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Your payee do not have a saving account", actual);
		
	}
	
	@Test
	public void testUpdatrePayeeWhenPayeeAccountNotPresent() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		Optional<CustomerAccountInfo> payAccount = Optional.empty();
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		String actual = payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Account number you entered is not a valid account number.", actual);
		
	}
	
	@Test
	public void testUpdatePayeeWhenPayeeWithSameNicknameExist() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		payeeInfoVO.setPayeeNickName("Sid");
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName())).thenReturn(oPayeeInfo);
		
		
		String actual =  payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Payee with the same same nickname already exists.. please try different name.", actual);
	}
	
	
	@Test
	public void testUpdatePayeeWhenEverythingOkay() {
		PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
		payeeInfoVO.setPayeeAccountNo("456");
		payeeInfoVO.setCustomerId("r@gmail.com");
		payeeInfoVO.setPayeeNickName("Sid");
		payeeInfoVO.setId(1);
		payeeInfoVO.setRemarks("Rent");
		
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(payeeInfoVO.getCustomerId())).thenReturn(cusAccount);
		
		CustomerAccountInfo payeeAccountInfo = new CustomerAccountInfo();
		payeeAccountInfo.setAccountNumber("456");
		payeeAccountInfo.setCustomerId("s@gmail.com");
		payeeAccountInfo.setStatus("Active");
		payeeAccountInfo.setAccountType("Saving");
		Optional<CustomerAccountInfo> payAccount = Optional.of(payeeAccountInfo);
		when(createAccountRepository.findByAccountNumber(payeeInfoVO.getPayeeAccountNo())).thenReturn(payAccount);	
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setCustomerId("r@gmail.com");
		payeeInfo.setPayeeNickName("Sid1");
		payeeInfo.setRemarks("Loan");
		payeeInfo.setDom(null);
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findByCustomerIdAndPayeeNickName(payeeInfoVO.getCustomerId(),payeeInfoVO.getPayeeNickName())).thenReturn(oPayeeInfo);
		
		when(payeeInfoRepository.findById(payeeInfoVO.getId())).thenReturn(oPayeeInfo);
		
		String actual =  payeeServiceImpl.updatePayee(payeeInfoVO);
		assertEquals("Payee information have been updated..", actual);
		assertEquals("Sid", oPayeeInfo.get().getPayeeNickName());
		assertEquals("Rent", oPayeeInfo.get().getRemarks());
		//assertEquals(new Timestamp(new Date().getTime()), oPayeeInfo.get().getDom());
		assertNotNull(oPayeeInfo.get().getDom());
	}

}
