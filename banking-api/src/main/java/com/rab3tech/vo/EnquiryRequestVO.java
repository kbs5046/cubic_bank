package com.rab3tech.vo;

public class EnquiryRequestVO {
	private String name;
	private String email;
	private String mobile;
	private String location;
	private String accType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	@Override
	public String toString() {
		return "EnquiryRequestVO [name=" + name + ", email=" + email + ", mobile=" + mobile + ", location=" + location
				+ ", accType=" + accType + "]";
	}
	
	

}
