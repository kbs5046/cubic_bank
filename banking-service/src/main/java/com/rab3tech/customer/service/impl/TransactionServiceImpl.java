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
import com.rab3tech.customer.dao.repository.TransactionRepository;
import com.rab3tech.customer.service.TransactionService;
import com.rab3tech.dao.entity.CustomerAccountInfo;
import com.rab3tech.dao.entity.PayeeInfo;
import com.rab3tech.dao.entity.Transaction;
import com.rab3tech.vo.PayeeInfoVO;
import com.rab3tech.vo.TransactionVO;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private CreateAccountRepository createAccountRepository;
	
	@Autowired
	private PayeeInfoRepository payeeInfoRepository;
	
	@Override
	public List<TransactionVO> getSummary(String username) {
		Optional<CustomerAccountInfo> cusAccount = createAccountRepository.findByCustomerId(username);
//		if(cusAccount.isPresent()) {
//			if(!cusAccount.get().getStatus().equalsIgnoreCase("ACTIVE")) {
//				return "Your account is not active.. Please contact bank first to activate your account";
//			}
//		}
//		else {
//			return "You do not have a valid account number";
//		}
		List<TransactionVO> transactionVOs = new ArrayList<TransactionVO>();
		List<Transaction> transactions = transactionRepository.findAllTransactions(cusAccount.get().getAccountNumber());
//		if(!transactions.isEmpty()) {
//			Collections.reverse(transactions);
//		}
		for(Transaction transaction: transactions) {
			TransactionVO transactionVO = new TransactionVO();
			BeanUtils.copyProperties(transaction, transactionVO);
			if(cusAccount.get().getAccountNumber().equalsIgnoreCase(transaction.getDebitAccountNo())) {
				transactionVO.setTransactionType("DEBIT");
			}
			else {
				transactionVO.setTransactionType("CREDIT");
			}
			PayeeInfoVO payeeInfoVO = new PayeeInfoVO();
			BeanUtils.copyProperties(transaction.getPayeeId(), payeeInfoVO);
			transactionVO.setPayeeInfoVO(payeeInfoVO);
			transactionVOs.add(transactionVO);
		}
		
		return transactionVOs;
	}
	
	@Override
	public String transferFund(TransactionVO transactionVO) {
		Optional<CustomerAccountInfo> cusAccount = createAccountRepository.findByCustomerId(transactionVO.getDebitCustomerId());
		if(cusAccount.isPresent()) {
			if(!cusAccount.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your account is not active.. Please contact bank first to activate your account";
			}
			if(cusAccount.get().getAvBalance() < transactionVO.getAmount()) {
				return "You do not have sufficient balance to make this transaction.";
			}
		}
		else {
			return "You do not have a valid account number";
		}
		
		PayeeInfo payeeInfo = payeeInfoRepository.findById(transactionVO.getPayeeId()).get();
		Optional<CustomerAccountInfo> payAccount = createAccountRepository.findByAccountNumber(payeeInfo.getPayeeAccountNo());
		if(payAccount.isPresent()) {
			if(!payAccount.get().getStatus().equalsIgnoreCase("ACTIVE")) {
				return "Your payee's account is not active..";
			}	
		}
		else {
			return "Your payee do not have a valid account..";
		}
		CustomerAccountInfo cusAccount1 = cusAccount.get();
		cusAccount1.setAvBalance(cusAccount1.getAvBalance() - transactionVO.getAmount());
		createAccountRepository.save(cusAccount1);
		CustomerAccountInfo payAccount1 = payAccount.get();
		payAccount1.setAvBalance(payAccount1.getAvBalance() + transactionVO.getAmount());
		createAccountRepository.save(payAccount1);
		Transaction transaction = new Transaction();
		BeanUtils.copyProperties(transactionVO, transaction);
		transaction.setDebitAccountNo(cusAccount1.getAccountNumber());
		transaction.setTransactionDate(new Timestamp(new Date().getTime()));
		transaction.setPayeeId(payeeInfo);
		transactionRepository.save(transaction);
		return "Fund amount of $"+transactionVO.getAmount()+" have been transfered to "+payeeInfo.getPayeeNickName();
	}

}
