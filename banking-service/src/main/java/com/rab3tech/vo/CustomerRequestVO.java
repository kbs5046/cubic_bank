package com.rab3tech.vo;

import java.sql.Timestamp;

public class CustomerRequestVO {
	private int requestId;
	private String refNumber;
	private Timestamp doe;
	private Timestamp doa;
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
		return "CustomerRequestVO [requestId=" + requestId + ", refNumber=" + refNumber + ", doe=" + doe + ", doa="
				+ doa + "]";
	}
	

}
