package com.rab3tech.dao.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="customer_transaction_tbl")
public class Transaction {
	private int transactionId;
	private String debitCustomerId;
	private String debitAccountNo;
	private float amount;
	private String reason;
	private Timestamp transactionDate;
	private PayeeInfo payeeId;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getTransactionId() {
		return transactionId;
	}

	public String getDebitCustomerId() {
		return debitCustomerId;
	}

	public void setDebitCustomerId(String debitCustomerId) {
		this.debitCustomerId = debitCustomerId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}
	public String getDebitAccountNo() {
		return debitAccountNo;
	}
	public void setDebitAccountNo(String debitAccountNo) {
		this.debitAccountNo = debitAccountNo;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public Timestamp  getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payeeId", nullable = false)
	public PayeeInfo getPayeeId() {
		return payeeId;
	}
	public void setPayeeId(PayeeInfo payeeId) {
		this.payeeId = payeeId;
	}

	@Override
	public String toString() {
		return "Transaction [transactionId=" + transactionId + ", debitCustomerId=" + debitCustomerId
				+ ", debitAccountNo=" + debitAccountNo + ", amount=" + amount + ", reason=" + reason
				+ ", transactionDate=" + transactionDate + ", payeeId=" + payeeId + "]";
	}
	
}
