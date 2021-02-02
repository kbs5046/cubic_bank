package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.rab3tech.customer.dao.repository.CustomerQuestionsAnsRepository;
import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.customer.dao.repository.SecurityQuestionsRepository;
import com.rab3tech.dao.entity.CustomerQuestionAnswer;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.SecurityQuestions;
import com.rab3tech.vo.CustomerSecurityQueAnsVO;
import com.rab3tech.vo.SecurityQuestionsVO;

@RunWith(MockitoJUnitRunner.class)
public class SecurityQuestionServiceImplTest {
	
	@Mock
	private SecurityQuestionsRepository questionsRepository;
	
	@Mock
	private LoginRepository loginRepository;
	
	@Mock
	private CustomerQuestionsAnsRepository customerQuestionsAnsRepository;
	
	@InjectMocks
	private SecurityQuestionServiceImpl securityQuestionServiceImpl;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindAnsByLoginid() {
		String username = "r@gmail.com";
		CustomerQuestionAnswer questionAnswer1 = new CustomerQuestionAnswer();
		questionAnswer1.setQuestion("school?");questionAnswer1.setAnswer("VMSS");questionAnswer1.setId(1);
		CustomerQuestionAnswer questionAnswer2 = new CustomerQuestionAnswer();
		questionAnswer2.setQuestion("movie?");questionAnswer2.setAnswer("Tumbbad");questionAnswer2.setId(2);	
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		customerQuestionAnswers.add(questionAnswer1);customerQuestionAnswers.add(questionAnswer2);
		when(customerQuestionsAnsRepository.findQuestionAnswer(username)).thenReturn(customerQuestionAnswers);
		
		CustomerSecurityQueAnsVO actual = securityQuestionServiceImpl.findAnsByLoginid(username);
		assertEquals("VMSS", actual.getSecurityQuestionAnswer1());
		assertEquals("Tumbbad", actual.getSecurityQuestionAnswer2());
	}
	
	@Test
	public void testGetQuestionByLoginidWhenFound() {
		String username = "r@gmail.com";
		CustomerQuestionAnswer questionAnswer1 = new CustomerQuestionAnswer();
		questionAnswer1.setQuestion("school?");
		Login login1 = new Login();
		login1.setLoginid("r@gmail.com");
		questionAnswer1.setLogin(login1);
		CustomerQuestionAnswer questionAnswer2 = new CustomerQuestionAnswer();
		questionAnswer2.setQuestion("movie?");
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		customerQuestionAnswers.add(questionAnswer1);customerQuestionAnswers.add(questionAnswer2);
		when(customerQuestionsAnsRepository.findQuestionAnswer(username)).thenReturn(customerQuestionAnswers);
		
		CustomerSecurityQueAnsVO actual = securityQuestionServiceImpl.getQuestionByLoginid(username);
		assertEquals("school?", actual.getSecurityQuestion1());
		assertEquals("movie?", actual.getSecurityQuestion2());
		assertEquals("r@gmail.com", actual.getLoginid());
		
	}
	
	@Test
	public void testGetQuestionByLoginidWhenNotFound() {
		String username = "r@gmail.com";
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		when(customerQuestionsAnsRepository.findQuestionAnswer(username)).thenReturn(customerQuestionAnswers);
		
		CustomerSecurityQueAnsVO actual = securityQuestionServiceImpl.getQuestionByLoginid(username);
		assertNull(actual);
		
	}
	
	
	@Test
	public void testSaveWhenQuestionAnswerExist() {
		CustomerSecurityQueAnsVO customerSecurityQueAnsVO = new CustomerSecurityQueAnsVO("1", "2", "virat",
				"erngineer", "r@gmail.com");
		
		CustomerQuestionAnswer questionAnswer1 = new CustomerQuestionAnswer();
		questionAnswer1.setQuestion("school?");questionAnswer1.setAnswer("VMSS");questionAnswer1.setId(1);
		CustomerQuestionAnswer questionAnswer2 = new CustomerQuestionAnswer();
		questionAnswer2.setQuestion("movie?");questionAnswer2.setAnswer("Tumbbad");questionAnswer2.setId(2);	
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		customerQuestionAnswers.add(questionAnswer1);customerQuestionAnswers.add(questionAnswer2);
		when(customerQuestionsAnsRepository.findQuestionAnswer(customerSecurityQueAnsVO.getLoginid())).thenReturn(customerQuestionAnswers);
		
		Login login = new Login();
		login.setLoginid("r@gmail.com");
		Optional<Login> oLogin = Optional.of(login);
		when(loginRepository.findById(customerSecurityQueAnsVO.getLoginid())).thenReturn(oLogin);
		
		SecurityQuestions questions1 = new SecurityQuestions();
		questions1.setQid(1);questions1.setQuestions("Job?");
		Optional<SecurityQuestions> optional1 = Optional.of(questions1);
		when(questionsRepository.findById(Integer.parseInt(customerSecurityQueAnsVO.getSecurityQuestion1()))).thenReturn(optional1);
		
		CustomerQuestionAnswer customerQuestionAnswer1=new CustomerQuestionAnswer();
		customerQuestionAnswer1.setQuestion(questions1.getQuestions());
 		customerQuestionAnswer1.setAnswer(customerSecurityQueAnsVO.getSecurityQuestionAnswer1());
 		customerQuestionAnswer1.setDoe(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer1.setDom(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer1.setLogin(login);
		
		SecurityQuestions questions2 = new SecurityQuestions();
		questions2.setQid(2);questions2.setQuestions("Player?");
		Optional<SecurityQuestions> optional2 = Optional.of(questions2);
		when(questionsRepository.findById(Integer.parseInt(customerSecurityQueAnsVO.getSecurityQuestion2()))).thenReturn(optional2);
		
		CustomerQuestionAnswer customerQuestionAnswer2=new CustomerQuestionAnswer();
		customerQuestionAnswer2.setQuestion(questions2.getQuestions());
 		customerQuestionAnswer2.setAnswer(customerSecurityQueAnsVO.getSecurityQuestionAnswer2());
 		customerQuestionAnswer2.setDoe(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer2.setDom(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer2.setLogin(login);
 		
 		securityQuestionServiceImpl.save(customerSecurityQueAnsVO);
 		assertEquals("Job?", customerQuestionAnswer1.getQuestion());
 		assertEquals("Player?", customerQuestionAnswer2.getQuestion());
 		assertEquals("erngineer", customerQuestionAnswer1.getAnswer());
 		assertEquals("virat", customerQuestionAnswer2.getAnswer());
 		verify(customerQuestionsAnsRepository, times(2)).save(any(CustomerQuestionAnswer.class));
 		verify(customerQuestionsAnsRepository,times(1)).deleteAll(customerQuestionAnswers);
		
	}

	@Test
	public void testSaveWhenQuestionAnswerNotExist() {
		CustomerSecurityQueAnsVO customerSecurityQueAnsVO = new CustomerSecurityQueAnsVO("1", "2", "virat",
				"erngineer", "r@gmail.com");
		
		List<CustomerQuestionAnswer> customerQuestionAnswers = new ArrayList<>();
		when(customerQuestionsAnsRepository.findQuestionAnswer(customerSecurityQueAnsVO.getLoginid())).thenReturn(customerQuestionAnswers);
		
		Login login = new Login();
		login.setLoginid("r@gmail.com");
		Optional<Login> oLogin = Optional.of(login);
		when(loginRepository.findById(customerSecurityQueAnsVO.getLoginid())).thenReturn(oLogin);
		
		SecurityQuestions questions1 = new SecurityQuestions();
		questions1.setQid(1);questions1.setQuestions("Job?");
		Optional<SecurityQuestions> optional1 = Optional.of(questions1);
		when(questionsRepository.findById(Integer.parseInt(customerSecurityQueAnsVO.getSecurityQuestion1()))).thenReturn(optional1);
		
		CustomerQuestionAnswer customerQuestionAnswer1=new CustomerQuestionAnswer();
		customerQuestionAnswer1.setQuestion(questions1.getQuestions());
 		customerQuestionAnswer1.setAnswer(customerSecurityQueAnsVO.getSecurityQuestionAnswer1());
 		customerQuestionAnswer1.setDoe(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer1.setDom(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer1.setLogin(login);
		
		SecurityQuestions questions2 = new SecurityQuestions();
		questions2.setQid(2);questions2.setQuestions("Player?");
		Optional<SecurityQuestions> optional2 = Optional.of(questions2);
		when(questionsRepository.findById(Integer.parseInt(customerSecurityQueAnsVO.getSecurityQuestion2()))).thenReturn(optional2);
		
		CustomerQuestionAnswer customerQuestionAnswer2=new CustomerQuestionAnswer();
		customerQuestionAnswer2.setQuestion(questions2.getQuestions());
 		customerQuestionAnswer2.setAnswer(customerSecurityQueAnsVO.getSecurityQuestionAnswer2());
 		customerQuestionAnswer2.setDoe(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer2.setDom(new Timestamp(new Date().getTime()));
 		customerQuestionAnswer2.setLogin(login);
 		
 		securityQuestionServiceImpl.save(customerSecurityQueAnsVO);
 		assertEquals("Job?", customerQuestionAnswer1.getQuestion());
 		assertEquals("Player?", customerQuestionAnswer2.getQuestion());
 		assertEquals("erngineer", customerQuestionAnswer1.getAnswer());
 		assertEquals("virat", customerQuestionAnswer2.getAnswer());
 		verify(customerQuestionsAnsRepository, times(2)).save(any(CustomerQuestionAnswer.class));
	}
	
	
	@Test
	public void testFindAll() {
		SecurityQuestions questions1 = new SecurityQuestions();
		questions1.setQid(1);questions1.setQuestions("Job?");questions1.setStatus("yes");
		SecurityQuestions questions2 = new SecurityQuestions();
		questions2.setQid(2);questions2.setQuestions("Player?");questions2.setStatus("yes");
		List<SecurityQuestions>  securityQuestions = new ArrayList<>();
		securityQuestions.add(questions1);
		securityQuestions.add(questions2);
		when(questionsRepository.findActiveQuestions("yes")).thenReturn(securityQuestions);
		
		
		List<SecurityQuestionsVO> actual = securityQuestionServiceImpl.findAll();
		assertEquals("Job?", actual.get(0).getQuestions());
		assertEquals("Player?", actual.get(1).getQuestions());
		assertEquals("yes",actual.get(0).getStatus());
		assertEquals("yes",actual.get(1).getStatus());
		
	}
	
	@Test
	public void testFindByid() {
		int id = 1;
		SecurityQuestions questions1 = new SecurityQuestions();
		questions1.setQid(1);questions1.setQuestions("Job?");questions1.setStatus("yes");
		Optional<SecurityQuestions> optional = Optional.of(questions1);
		when(questionsRepository.findById(id)).thenReturn(optional);
		
		SecurityQuestionsVO  actual = securityQuestionServiceImpl.findByid(id);
		assertEquals("Job?", actual.getQuestions());
		assertEquals("yes",actual.getStatus());
		assertEquals(1, actual.getQid());
	}
	
	

}
