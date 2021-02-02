package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

import com.rab3tech.admin.dao.repository.RequestTypeRepository;
import com.rab3tech.dao.entity.RequestType;
import com.rab3tech.vo.RequestTypeVO;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceImplTest {
	@Mock
	private RequestTypeRepository requestTypeRepository;
	
	@InjectMocks
	private RequestServiceImpl requestServiceImpl;

	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testfindActiveRequests() {
		RequestType requestType1 = new RequestType(1, "RQ1", "loan", "loan", 1);
		RequestType requestType2 = new RequestType(2, "RQ2", "checkbook", "checkbook", 1);
		List<RequestType> requestTypes = new ArrayList<>();
		requestTypes.add(requestType1);
		requestTypes.add(requestType2);
		when(requestTypeRepository.findByStatus(1)).thenReturn(requestTypes);
		
		List<RequestTypeVO> actual = requestServiceImpl.findActiveRequests();
		assertEquals(1, actual.get(0).getStatus());
		assertEquals(1, actual.get(1).getStatus());
		assertEquals("loan", actual.get(0).getName());
		assertEquals("checkbook", actual.get(1).getName());
	}
	
	@Test
	public void testfindRequestTypeByIdWhenRequestTypePresent() {
		int reauestId = 1;
		RequestType requestType1 = new RequestType(1, "RQ1", "loan", "loan", 1);
		Optional<RequestType> requestType = Optional.of(requestType1);
		when(requestTypeRepository.findById(reauestId)).thenReturn(requestType);
		
		RequestTypeVO actual = requestServiceImpl.findRequestTypeById(reauestId);
		assertEquals(1, actual.getId());
		assertEquals("loan", actual.getName());
		
	}
	
	@Test
	public void testfindRequestTypeByIdWhenRequestTypeNotPresent() {
		int reauestId = 1;
		Optional<RequestType> requestType = Optional.empty();
		when(requestTypeRepository.findById(reauestId)).thenReturn(requestType);
		
		RequestTypeVO actual = requestServiceImpl.findRequestTypeById(reauestId);
		assertNull(actual);
		
	}
}
