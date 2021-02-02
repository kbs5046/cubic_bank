package com.rab3tech.vo;

import java.sql.Timestamp;

public class TransactionVO {
	private int payeeId;
	private float amount;
	private String reason;
	private String debitCustomerId;
	private String transactionType;
	private Timestamp transactionDate;
	private PayeeInfoVO payeeInfoVO;

	
	
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public Timestamp getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}
	public PayeeInfoVO getPayeeInfoVO() {
		return payeeInfoVO;
	}
	public void setPayeeInfoVO(PayeeInfoVO payeeInfoVO) {
		this.payeeInfoVO = payeeInfoVO;
	}
	public int getPayeeId() {
		return payeeId;
	}
	public void setPayeeId(int payeeId) {
		this.payeeId = payeeId;
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
	public String getDebitCustomerId() {
		return debitCustomerId;
	}
	public void setDebitCustomerId(String debitCustomerId) {
		this.debitCustomerId = debitCustomerId;
	}
	@Override
	public String toString() {
		return "TransactionVO [payeeId=" + payeeId + ", amount=" + amount + ", reason=" + reason + ", debitCustomerId="
				+ debitCustomerId + ", transactionType=" + transactionType + ", transactionDate=" + transactionDate
				+ ", payeeInfoVO=" + payeeInfoVO + "]";
	}
	
}
