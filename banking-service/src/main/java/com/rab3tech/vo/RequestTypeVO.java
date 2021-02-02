package com.rab3tech.vo;

public class RequestTypeVO {
	private int id;
	private String code;
	private String name;
	private String description;
	private int status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "RequestTypeVO [id=" + id + ", code=" + code + ", name=" + name + ", description=" + description
				+ ", status=" + status + "]";
	}
	
	

}
