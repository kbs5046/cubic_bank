package com.rab3tech.customer.service;

import java.util.List;

import com.rab3tech.vo.RequestTypeVO;

public interface RequestService {

	List<RequestTypeVO> findActiveRequests();

	RequestTypeVO findRequestTypeById(int reauestId);

}
