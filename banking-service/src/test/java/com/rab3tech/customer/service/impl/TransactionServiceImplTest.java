package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

import com.rab3tech.customer.dao.repository.CreateAccountRepository;
import com.rab3tech.customer.dao.repository.PayeeInfoRepository;
import com.rab3tech.customer.dao.repository.TransactionRepository;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.dao.entity.Transaction;
import com.rab3tech.vo.TransactionVO;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTest {
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@Mock
	private CreateAccountRepository createAccountRepository;
	
	@Mock
	private PayeeInfoRepository payeeInfoRepository;
	
	@InjectMocks
	private TransactionServiceImpl transactionServiceImpl;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	
	@Test
	public void testGetSummaryWhenDebitAndCredit() {
		String username = "r@gmail.com";
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(username)).thenReturn(cusAccount);
		
		Transaction transaction1 = new Transaction();
		transaction1.setDebitAccountNo("123");
		PayeeInfo payeeInfo1 = new PayeeInfo();
		payeeInfo1.setPayeeAccountNo("456");
		payeeInfo1.setCustomerId("r@gmail.com");
		transaction1.setPayeeId(payeeInfo1);
		
		Transaction transaction2 = new Transaction();
		transaction2.setDebitAccountNo("456");
		PayeeInfo payeeInfo2 = new PayeeInfo();
		payeeInfo2.setPayeeAccountNo("123");
		payeeInfo2.setCustomerId("abc");
		transaction2.setPayeeId(payeeInfo2);
		
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction1);
		transactions.add(transaction2);
		
		when(transactionRepository.findAllTransactions(cusAccount.get().getAccountNumber())).thenReturn(transactions);
		
		List<TransactionVO> actual = transactionServiceImpl.getSummary(username);
		assertEquals("DEBIT", actual.get(0).getTransactionType());
		assertEquals("456", actual.get(0).getPayeeInfoVO().getPayeeAccountNo());
		assertEquals("r@gmail.com", actual.get(0).getPayeeInfoVO().getCustomerId());
		assertEquals("CREDIT", actual.get(1).getTransactionType());
		assertEquals("123", actual.get(1).getPayeeInfoVO().getPayeeAccountNo());
		assertEquals("abc", actual.get(1).getPayeeInfoVO().getCustomerId());
	}
	
	@Test
	public void testTransferFundWhenDedbtAccountNotActive() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("NotActive");
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals("Your account is not active.. Please contact bank first to activate your account", actual);
		
	}
	
	@Test
	public void testTransferFundWhenDedbtAccountIsSortAmount() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAvBalance(50);
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals("You do not have sufficient balance to make this transaction.", actual);
		
	}
	
	@Test
	public void testTransferFundWhenDedbtAccountNotPresent() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		Optional<CustomerAccountInfo> cusAccount = Optional.empty();
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals("You do not have a valid account number", actual);
		
	}
	
	@Test
	public void testTransferFundWhenPayeetAccountNotActive() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAvBalance(500);
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findById(transactionVO.getPayeeId())).thenReturn(oPayeeInfo);
		
		CustomerAccountInfo payAccount = new CustomerAccountInfo();
		payAccount.setAccountNumber("456");
		payAccount.setStatus("NotActive");
		Optional<CustomerAccountInfo> OpayAccount = Optional.of(payAccount);
		when(createAccountRepository.findByAccountNumber(payeeInfo.getPayeeAccountNo())).thenReturn(OpayAccount);
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals("Your payee's account is not active..", actual);
		
	}
	
	@Test
	public void testTransferFundWhenPayeetAccountNotPresent() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAvBalance(500);
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findById(transactionVO.getPayeeId())).thenReturn(oPayeeInfo);
		
		Optional<CustomerAccountInfo> OpayAccount = Optional.empty();
		when(createAccountRepository.findByAccountNumber(payeeInfo.getPayeeAccountNo())).thenReturn(OpayAccount);
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals("Your payee do not have a valid account..", actual);
		
	}
	
	@Test
	public void testTransferFundWhenEverythingOkay() {
		TransactionVO transactionVO = new TransactionVO();
		transactionVO.setAmount(100);
		transactionVO.setDebitCustomerId("r@gmail.com");
		transactionVO.setPayeeId(5);
		
		CustomerAccountInfo customerAccountInfo = new CustomerAccountInfo();
		customerAccountInfo.setAccountNumber("123");
		customerAccountInfo.setCustomerId("r@gmail.com");
		customerAccountInfo.setStatus("Active");
		customerAccountInfo.setAvBalance(500);
		Optional<CustomerAccountInfo> cusAccount = Optional.of(customerAccountInfo);
		when(createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId())).thenReturn(cusAccount);
		
		
		PayeeInfo payeeInfo = new PayeeInfo();
		payeeInfo.setPayeeAccountNo("456");
		payeeInfo.setPayeeNickName("Sid");
		Optional<PayeeInfo> oPayeeInfo = Optional.of(payeeInfo);
		when(payeeInfoRepository.findById(transactionVO.getPayeeId())).thenReturn(oPayeeInfo);
		
		CustomerAccountInfo OpayAccount = new CustomerAccountInfo();
		OpayAccount.setAccountNumber("456");
		OpayAccount.setStatus("Active");
		OpayAccount.setAvBalance(5);
		OpayAccount.setCustomerId("s@gmail.com");
		Optional<CustomerAccountInfo> payAccount = Optional.of(OpayAccount);
		when(createAccountRepository.findByAccountNumber(payeeInfo.getPayeeAccountNo())).thenReturn(payAccount);
		
		
		String actual = transactionServiceImpl.transferFund(transactionVO);
		assertEquals( "Fund amount of $"+transactionVO.getAmount()+" have been transfered to "+payeeInfo.getPayeeNickName(), actual);
		assertEquals(105, payAccount.get().getAvBalance(), 0.00001);
		assertEquals(400, cusAccount.get().getAvBalance(), 0.00001);
		verify(createAccountRepository, times(2)).save(any(CustomerAccountInfo.class));
		verify(transactionRepository, times(1)).save(any(Transaction.class));
		verifyNoMoreInteractions(transactionRepository);
		
	}

}
