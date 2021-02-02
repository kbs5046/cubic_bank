package com.rab3tech.admin.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.rab3tech.admin.dao.repository.CustomerSecurityQuestionsRepository;
import com.rab3tech.dao.entity.SecurityQuestions;
import com.rab3tech.vo.SecurityQuestionsVO;

@RunWith(MockitoJUnitRunner.class)
public class CustomerSecurityQuestionsServiceImplTest {
	
	@Mock
	private CustomerSecurityQuestionsRepository customerSecurityQuestionsDao;
	
	@InjectMocks
	private CustomerSecurityQuestionsServiceImpl customerSecurityQuestionsServiceImpl;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindSecurityQuestions() {
		SecurityQuestions questions1 = new SecurityQuestions();
		questions1.setQid(1);
		questions1.setOwner("Ram");
		questions1.setCreatedate(new Timestamp(new Date().getTime()));
		SecurityQuestions questions2 = new SecurityQuestions(0,"school?","yes","me",new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()));
		List<SecurityQuestions> securityQuestions = new ArrayList<>();
		securityQuestions.add(questions1);
		securityQuestions.add(questions2);
		when( customerSecurityQuestionsDao.findAll()).thenReturn(securityQuestions);
		
		List<SecurityQuestionsVO> actual = customerSecurityQuestionsServiceImpl.findSecurityQuestions();
		assertEquals(1, actual.get(0).getQid());
		assertEquals("Ram", actual.get(0).getOwner());
		assertNotNull(actual.get(0).getCreatedate());
		assertEquals("school?", actual.get(1).getQuestions());
		assertEquals("yes", actual.get(1).getStatus());
		assertNotNull(actual.get(1).getUpdatedate());
		
	}
	
	@Test
	public void testUpdateStatusWhenActive() {
		int qid = 1;
		SecurityQuestions securityQuestions = new SecurityQuestions(1,"school?","yes","me",new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()));
		Optional<SecurityQuestions> oSecurityQuestions = Optional.of(securityQuestions);
		when(customerSecurityQuestionsDao.findById(qid)).thenReturn(oSecurityQuestions);
		
		customerSecurityQuestionsServiceImpl.updateStatus(qid);
		assertEquals("no", oSecurityQuestions.get().getStatus());	
		
	}
	
	@Test
	public void testUpdateStatusWhenNotActive() {
		int qid = 1;
		SecurityQuestions securityQuestions = new SecurityQuestions(1,"school?","no","me",new Timestamp(new Date().getTime()),new Timestamp(new Date().getTime()));
		Optional<SecurityQuestions> oSecurityQuestions = Optional.of(securityQuestions);
		when(customerSecurityQuestionsDao.findById(qid)).thenReturn(oSecurityQuestions);
		
		customerSecurityQuestionsServiceImpl.updateStatus(qid);
		assertEquals("yes", oSecurityQuestions.get().getStatus());	
		
	}
	
	@Test
	public void testAddSecurityQuestion() {
		String question = "school?";
		String loginid = "admin";
		customerSecurityQuestionsServiceImpl.addSecurityQuestion(question, loginid);
		
		verify(customerSecurityQuestionsDao,times(1)).save(any(SecurityQuestions.class));
		verifyNoMoreInteractions(customerSecurityQuestionsDao);
		
	}

}
