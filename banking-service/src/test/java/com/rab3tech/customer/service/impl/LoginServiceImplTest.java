package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.rab3tech.customer.dao.repository.LoginRepository;
import com.rab3tech.dao.entity.Login;
import com.rab3tech.dao.entity.Role;
import com.rab3tech.vo.ChangePasswordVO;
import com.rab3tech.vo.LoginVO;


@RunWith(MockitoJUnitRunner.class)
public class LoginServiceImplTest {
	
	@Mock
	private LoginRepository loginRepository;
	
	@Mock
 	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@InjectMocks
	private LoginServiceImpl loginServiceImpl;
	
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this); //Initializing mocking for each test cases
	}

	@Test
	public void testFindUserByUsernameWhenExist() {
		Login login=new Login();
		login.setEmail("nagen@gmail.com");
		login.setLoginid("owowo282");
		login.setPassword("cool@1234");
		Set<Role> rolesSet=new HashSet<>();
		Role role=new Role();
		role.setName("ADMIN");
		role.setRid(100);
		rolesSet.add(role);
		login.setRoles(rolesSet);
		Optional<Login> ologin=Optional.of(login);
		when(loginRepository.findByLoginid("nagen@gmail.com")).thenReturn(ologin);
		Optional<LoginVO>   optional=loginServiceImpl.findUserByUsername("nagen@gmail.com");
		assertTrue(optional.isPresent());
		assertEquals("nagen@gmail.com", optional.get().getEmail());
	}
	
	@Test
	public void testFindUserByUsernameWhenNotExist() {
		Optional<Login> ologin=Optional.empty();
		when(loginRepository.findByLoginid("nagen@gmail.com")).thenReturn(ologin);
		Optional<LoginVO>   optional=loginServiceImpl.findUserByUsername("nagen@gmail.com");
		assertFalse(optional.isPresent());
	}

	@Test
	public void testAuthUserWhenExist() {
		Login login=new Login();
		login.setEmail("nagen@gmail.com");
		login.setLoginid("owowo282");
		login.setPassword("cool@1234");
		Set<Role> rolesSet=new HashSet<>();
		Role role=new Role();
		role.setName("ADMIN");
		role.setRid(100);
		rolesSet.add(role);
		login.setRoles(rolesSet);
		Optional<Login> ologin=Optional.of(login);
		
		LoginVO loginVO=new LoginVO();
		loginVO.setUsername("nagen@gmail.com");
		loginVO.setPassword("cool@1234");
		when(loginRepository.findByLoginidAndPassword("nagen@gmail.com","cool@1234")).thenReturn(ologin);
		Optional<LoginVO> loginVO2=loginServiceImpl.authUser(loginVO);
		assertTrue(loginVO2.isPresent());
		assertEquals("cool@1234", loginVO2.get().getPassword());
		assertEquals("ADMIN", loginVO2.get().getRoles().get(0));
	}
	
	@Test
	public void testAuthUserWhenNotExist() {
		Optional<Login> ologin=Optional.empty();
		LoginVO loginVO=new LoginVO();
		loginVO.setUsername("nagen@gmail.com");
		loginVO.setPassword("cool@1234");
		when(loginRepository.findByLoginidAndPassword("nagen@gmail.com","cool@1234")).thenReturn(ologin);
		Optional<LoginVO> loginVO2=loginServiceImpl.authUser(loginVO);
		assertFalse(loginVO2.isPresent());
	}
	
	@Test
	public void testChangePassword() {
		ChangePasswordVO changePasswordVO = new ChangePasswordVO();
		changePasswordVO.setCurrentPassword("cool@1234");
		changePasswordVO.setNewPassword("ram");
		changePasswordVO.setLoginid("owowo282");
		String encodedPassword = "ramesh";
		when(bCryptPasswordEncoder.encode(changePasswordVO.getNewPassword())).thenReturn(encodedPassword);
		Login login = new Login();
		login.setEmail("nagen@gmail.com");
		login.setLoginid("owowo282");
		login.setPassword("ramesh");
		login.setLlt(new Timestamp(new Date().getTime()));
		Optional<Login> ologin=Optional.of(login);
		when(loginRepository.findByLoginid("owowo282")).thenReturn(ologin);
		loginServiceImpl.changePassword(changePasswordVO);
		assertEquals(encodedPassword,ologin.get().getPassword());
		//assertEquals(new Timestamp(new Date().getTime()), ologin.get().getLlt());
		verify(loginRepository,times(1)).findByLoginid("owowo282");
		verifyNoMoreInteractions(loginRepository);	
	}
	
	@Test
	public void testCheckPasswordValidWhenMatches() {
		Login login = new Login();
		login.setEmail("nagen@gmail.com");
		login.setLoginid("owowo282");
		login.setPassword("ramesh");
		Optional<Login> ologin=Optional.of(login);
		when(loginRepository.findByLoginid("owowo282")).thenReturn(ologin);
		
		when(bCryptPasswordEncoder.matches("ramesh",login.getPassword())).thenReturn(true);
		
		boolean result = loginServiceImpl.checkPasswordValid("owowo282","ramesh");
		assertTrue(result);
		verify(loginRepository,times(1)).findByLoginid("owowo282");
		verifyNoMoreInteractions(loginRepository);	
		
	}
	
	@Test
	public void testCheckPasswordValidWhenNoMatche() {
		Login login = new Login();
		login.setEmail("nagen@gmail.com");
		login.setLoginid("owowo282");
		login.setPassword("ramesh");
		Optional<Login> ologin=Optional.of(login);
		when(loginRepository.findByLoginid("owowo282")).thenReturn(ologin);
		
		when(bCryptPasswordEncoder.matches("ram",login.getPassword())).thenReturn(false);
		
		boolean result = loginServiceImpl.checkPasswordValid("owowo282","ram");
		assertFalse(result);
		verify(loginRepository,times(1)).findByLoginid("owowo282");
		verifyNoMoreInteractions(loginRepository);	
		
	}

}
