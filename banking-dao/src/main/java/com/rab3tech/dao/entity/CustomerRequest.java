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
@Table(name = "customer_request_tbl")
public class CustomerRequest {
	private int requestId;
	private String refNumber;
	private Customer customerId;
	private RequestType requestType;
	private AccountStatus accountStatus;
	private Timestamp doe;
	private Timestamp doa;
	
	public CustomerRequest() {
		
	}
	
	public CustomerRequest(int requestId, Customer customerId, RequestType requestType, AccountStatus accountStatus,
			Timestamp doe, Timestamp doa) {
		super();
		this.requestId = requestId;
		this.customerId = customerId;
		this.requestType = requestType;
		this.accountStatus = accountStatus;
		this.doe = doe;
		this.doa = doa;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	
	
	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customerId", nullable = false)
	public Customer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Customer customerId) {
		this.customerId = customerId;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestType", nullable = false)
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	@OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "accountStatus", nullable = false)
	public AccountStatus getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}
	public Timestamp getDoe() {
		return doe;
	}
	public void setDoe(Timestamp doe) {
		this.doe = doe;
	}
	public Timestamp getDoa() {
		return doa;
	}
	public void setDoa(Timestamp doa) {
		this.doa = doa;
	}

	@Override
	public String toString() {
		return "CustomerRequest [requestId=" + requestId + ", refNumber=" + refNumber + ", customerId=" + customerId
				+ ", requestType=" + requestType + ", accountStatus=" + accountStatus + ", doe=" + doe + ", doa=" + doa
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerRequest other = (CustomerRequest) obj;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		return true;
	}
	
	
	
}
