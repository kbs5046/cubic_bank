package com.rab3tech.customer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rab3tech.admin.dao.repository.RequestTypeRepository;
import com.rab3tech.customer.service.RequestService;
import com.rab3tech.dao.entity.RequestType;
import com.rab3tech.vo.RequestTypeVO;

@Transactional
@Service
public class RequestServiceImpl implements RequestService {
	
	@Autowired
	private RequestTypeRepository requestTypeRepository;
	
	@Override
	public List<RequestTypeVO> findActiveRequests() {
		List<RequestType> requestTypes = requestTypeRepository.findByStatus(1);
		List<RequestTypeVO> requestTypeVOs = new ArrayList<>();
		for(RequestType request: requestTypes) {
			RequestTypeVO vo = new RequestTypeVO();
			BeanUtils.copyProperties(request, vo);
			requestTypeVOs.add(vo);
		}
		return requestTypeVOs;
	}

	@Override
	public RequestTypeVO findRequestTypeById(int reauestId) {
		Optional<RequestType> requestType = requestTypeRepository.findById(reauestId);
		RequestTypeVO requestTypeVO = null;
		if(requestType.isPresent()) {
			requestTypeVO = new RequestTypeVO();
			BeanUtils.copyProperties(requestType.get(), requestTypeVO);
			return requestTypeVO;
		}
		return requestTypeVO;
	}

}
