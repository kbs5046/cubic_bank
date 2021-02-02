package com.rab3tech.customer.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import com.rab3tech.admin.dao.repository.AccountStatusRepository;
import com.rab3tech.admin.dao.repository.AccountTypeRepository;
import com.rab3tech.customer.dao.repository.CustomerAccountEnquiryRepository;
import com.rab3tech.dao.entity.AccountStatus;
import com.rab3tech.dao.entity.AccountType;
import com.rab3tech.dao.entity.CustomerSaving;
import com.rab3tech.email.service.EmailService;
import com.rab3tech.service.exception.BankServiceException;
import com.rab3tech.utils.AccountStatusEnum;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.EmailVO;


@RunWith(MockitoJUnitRunner.class)
public class CustomerEnquiryServiceImplTest {

	@Mock
	private CustomerAccountEnquiryRepository customerAccountEnquiryRepository;
	
	@Mock
	private AccountTypeRepository accountTypeRepository;
	
	@Mock	
	private AccountStatusRepository accountStatusRepository;
	
	@Mock
	private EmailService emailService;
	
	@InjectMocks
	private CustomerEnquiryServiceImpl customerEnquiryServiceImpl;
	
	@Before
	public void init() {
		 MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindPendingEnquiryWhenResult() {
		 List<CustomerSaving> customerSavings=new ArrayList<>();
		 CustomerSaving customerSaving1=new CustomerSaving(122,"nagendra","nagen@gmail.com","02390","NA","92828ns8w3",null,null,null,"A435");
		 CustomerSaving customerSaving2=new CustomerSaving(133,"moshi","moshi@gmail.com","345435","NA","346456",null,null,null,"A0393");
		 customerSaving1.setAccType(new AccountType(1, "code1", "saving","saving"));
		 customerSaving2.setStatus(new AccountStatus(2,"code2","approved","approved"));
		 customerSavings.add(customerSaving1);
		 customerSavings.add(customerSaving2);
		 when(customerAccountEnquiryRepository.findPendingEnquiries(AccountStatusEnum.PENDING.name())).thenReturn(customerSavings);
		 List<CustomerSavingVO> customerSavingVOs=customerEnquiryServiceImpl.findPendingEnquiry();
		 assertNotNull(customerSavingVOs);
		 assertEquals(customerSavingVOs.size(),2);
		 assertEquals(customerSavingVOs.get(0).getName(),"nagendra");
		 assertEquals(customerSavingVOs.get(0).getEmail(),"nagen@gmail.com");
		 assertEquals(customerSavingVOs.get(1).getName(),"moshi");
		 assertEquals(customerSavingVOs.get(1).getEmail(),"moshi@gmail.com");
		 assertEquals("saving", customerSavingVOs.get(0).getAccType());
		 assertEquals("approved", customerSavingVOs.get(1).getStatus());
		 verify(customerAccountEnquiryRepository, times(1)).findPendingEnquiries(AccountStatusEnum.PENDING.name());
	     verifyNoMoreInteractions(customerAccountEnquiryRepository);
	}
	
	@Test
	public void testFindPendingEnquiryWhenNoResult() {
		 List<CustomerSaving> customerSavings=new ArrayList<>();
		 when(customerAccountEnquiryRepository.findPendingEnquiries(AccountStatusEnum.PENDING.name())).thenReturn(customerSavings);
		 List<CustomerSavingVO> customerSavingVOs=customerEnquiryServiceImpl.findPendingEnquiry();
		 assertNotNull(customerSavingVOs);
		 assertEquals(customerSavingVOs.size(),0);
		 verify(customerAccountEnquiryRepository, times(1)).findPendingEnquiries(AccountStatusEnum.PENDING.name());
	     verifyNoMoreInteractions(customerAccountEnquiryRepository);
	}


	@Test
	public void testEmailNotExistFalse() {
			CustomerSaving customerSaving=new CustomerSaving(122,"Cubic","cubic@gmail.com","02390","NA","92828ns8w3",null,null,null,"A435");
			Optional<CustomerSaving> optional=Optional.of(customerSaving);
			when(customerAccountEnquiryRepository.findByEmail("cubic@gmail.com")).thenReturn(optional);
			boolean result=customerEnquiryServiceImpl.emailNotExist("cubic@gmail.com");
			
			assertFalse(result);
	}
	
   @Test
	public void testEmailNotExistTrue() {
			Optional<CustomerSaving> optional=Optional.empty();
			when(customerAccountEnquiryRepository.findByEmail("cubic@gmail.com")).thenReturn(optional);
			boolean result=customerEnquiryServiceImpl.emailNotExist("cubic@gmail.com");
			assertTrue(result);
	   	    verify(customerAccountEnquiryRepository, times(1)).findByEmail("cubic@gmail.com");
		    verifyNoMoreInteractions(customerAccountEnquiryRepository);
	}

	@Test
	public void testUpdateEnquiryRegId() {
		int csaid = 1;
		String ucrid = "$123";
		CustomerSaving customerSavingEntity = new CustomerSaving();
		customerSavingEntity.setCsaid(1);
		customerSavingEntity.setUcrid("cool");
		Optional<CustomerSaving> oCustomerSavingEntity = Optional.of(customerSavingEntity);
		when(customerAccountEnquiryRepository.findById(csaid)).thenReturn(oCustomerSavingEntity);
		
		oCustomerSavingEntity.get().setUcrid(ucrid);
		String actual = customerEnquiryServiceImpl.updateEnquiryRegId(csaid, ucrid);
		
		assertEquals("done", actual);
		assertEquals(1,oCustomerSavingEntity.get().getCsaid());
		assertEquals("$123",oCustomerSavingEntity.get().getUcrid());
		verify(customerAccountEnquiryRepository, times(1)).findById(csaid);
	    verifyNoMoreInteractions(customerAccountEnquiryRepository);
	}

	@Test
	public void testFindById() {
		int csaid = 1;	
		CustomerSaving customerSavingEntity = new CustomerSaving();
		customerSavingEntity.setCsaid(1);
		customerSavingEntity.setStatus(new AccountStatus(1,"code1", "pending","pending"));
		customerSavingEntity.setAccType(new AccountType(1, "code1", "saving","saving"));
		Optional<CustomerSaving> oCustomerSavingEntity = Optional.of(customerSavingEntity);
		when(customerAccountEnquiryRepository.findById(csaid)).thenReturn(oCustomerSavingEntity);
		
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		BeanUtils.copyProperties(oCustomerSavingEntity.get(), customerSavingVO, new String[] { "accType", "status" });
		customerSavingVO.setAccType(oCustomerSavingEntity.get().getAccType().getName());
		customerSavingVO.setStatus(oCustomerSavingEntity.get().getStatus().getName());
		
		CustomerSavingVO actual= customerEnquiryServiceImpl.findById(csaid);
		assertEquals("pending", actual.getStatus());
		assertEquals("saving", actual.getAccType());
		assertEquals(csaid,actual.getCsaid());
		verify(customerAccountEnquiryRepository, times(1)).findById(csaid);
	    verifyNoMoreInteractions(customerAccountEnquiryRepository);
		
	}

	@Test
	public void testChangeEnquiryStatus() {
		CustomerSaving customerSavingEntity = new CustomerSaving();
		customerSavingEntity.setCsaid(1);
		customerSavingEntity.setStatus(new AccountStatus(1,"code1", "pending","pending"));
		customerSavingEntity.setAccType(new AccountType(1, "code1", "saving","saving"));
		Optional<CustomerSaving> oCustomerSavingEntity = Optional.of(customerSavingEntity);
		when(customerAccountEnquiryRepository.findById(1)).thenReturn(oCustomerSavingEntity);
		AccountStatus accountStatus = new AccountStatus(2,"code2","approved","approved");
		Optional<AccountStatus> oAccountStatus = Optional.of(accountStatus);
		when(accountStatusRepository.findByName("approved")).thenReturn(oAccountStatus);
		oCustomerSavingEntity.get().setStatus(oAccountStatus.get());
		
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		BeanUtils.copyProperties(oCustomerSavingEntity.get(), customerSavingVO, new String[] { "accType", "status" });
		customerSavingVO.setAccType(oCustomerSavingEntity.get().getAccType().getName());
		customerSavingVO.setStatus(oCustomerSavingEntity.get().getStatus().getName());
		
		CustomerSavingVO result= customerEnquiryServiceImpl.changeEnquiryStatus(1,"approved");
		assertEquals("approved", result.getStatus());
		assertEquals("saving", result.getAccType());
		assertEquals(1,result.getCsaid());
		verify(customerAccountEnquiryRepository, times(1)).findById(1);
	    verifyNoMoreInteractions(customerAccountEnquiryRepository);
	    verify(accountStatusRepository, times(1)).findByName("approved");
	    verifyNoMoreInteractions(accountStatusRepository);

		
	}
	
	@Test
	public void testFindCustomerEnquiryByUuidWhenExist() {
		CustomerSaving customerSaving = new CustomerSaving();
		customerSaving.setUcrid("$123");
		customerSaving.setCsaid(1);
		customerSaving.setAccType(new AccountType(1, "code1", "saving","saving"));
		customerSaving.setStatus(new AccountStatus(1,"code1", "pending","pending"));
		Optional<CustomerSaving> oCustomerSaving = Optional.of(customerSaving);
		when(customerAccountEnquiryRepository.findByUcrid("$123")).thenReturn(oCustomerSaving);
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		BeanUtils.copyProperties(oCustomerSaving.get(), customerSavingVO, new String[] { "accType", "status" });
		customerSavingVO.setAccType(oCustomerSaving.get().getAccType().getName());
		customerSavingVO.setStatus(oCustomerSaving.get().getStatus().getName());
		
		Optional<CustomerSavingVO> optional = customerEnquiryServiceImpl.findCustomerEnquiryByUuid("$123");
		assertEquals(1, optional.get().getCsaid());
		assertEquals("$123", optional.get().getUcrid());
		assertEquals("saving", optional.get().getAccType());
		assertEquals("pending", optional.get().getStatus());
		verify(customerAccountEnquiryRepository, times(1)).findByUcrid("$123");
		verifyNoMoreInteractions(customerAccountEnquiryRepository);
		
		
	}
	

	@Test
	public void testFindCustomerEnquiryByUuidWhenNotExist() {
		Optional<CustomerSaving> oCustomerSaving = Optional.empty();
		when(customerAccountEnquiryRepository.findByUcrid("$123")).thenReturn(oCustomerSaving);
		
		Optional<CustomerSavingVO> optional = customerEnquiryServiceImpl.findCustomerEnquiryByUuid("$123");
		
		assertFalse(optional.isPresent());
		verify(customerAccountEnquiryRepository, times(1)).findByUcrid("$123");
		verifyNoMoreInteractions(customerAccountEnquiryRepository);
		
	}

	@Test
	public void testDeleteById() {
		customerEnquiryServiceImpl.deleteById(1);
		// verify the mocks
        verify(customerAccountEnquiryRepository, times(1)).deleteById(1);
        verifyNoMoreInteractions(customerAccountEnquiryRepository);
		
	}
	
	@Test
	public void testfindAll() {
		CustomerSaving customerSaving1 = new CustomerSaving();
		customerSaving1.setCsaid(1);
		customerSaving1.setName("Ramesh");
		customerSaving1.setAccType(new AccountType(1, "code1", "saving","saving"));
		CustomerSaving customerSaving2 = new CustomerSaving();
		customerSaving2.setCsaid(2);
		customerSaving2.setName("Sitaula");
		customerSaving2.setStatus(new AccountStatus(2,"code2","approved","approved"));
		List<CustomerSaving> customerSavingList = new ArrayList<>();
		customerSavingList.add(customerSaving1);
		customerSavingList.add(customerSaving2);
		
		when(customerAccountEnquiryRepository.findAll()).thenReturn(customerSavingList);
		List<CustomerSavingVO> customerSavingVOs = customerEnquiryServiceImpl.findAll();
		
		assertEquals(1, customerSavingVOs.get(0).getCsaid());
		assertEquals(2, customerSavingVOs.get(1).getCsaid());
		assertEquals("Ramesh", customerSavingVOs.get(0).getName());
		assertEquals("Sitaula", customerSavingVOs.get(1).getName());
		assertEquals("saving", customerSavingVOs.get(0).getAccType());
		assertEquals("approved", customerSavingVOs.get(1).getStatus());
		verify(customerAccountEnquiryRepository, times(1)).findAll();
        verifyNoMoreInteractions(customerAccountEnquiryRepository);
	}
	

	@Test
	public void testSaveWhenAccountTypePresent() {
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		customerSavingVO.setAccType("pending");
		customerSavingVO.setEmail("r@gmail.com");
		CustomerSaving entity = new CustomerSaving();
		BeanUtils.copyProperties(customerSavingVO, entity, new String[] { "accType", "status" });
		AccountType accountType =new AccountType(1, "code1", "saving","saving");
		Optional<AccountType> optional = Optional.of(accountType);
		when(accountTypeRepository.findByName(customerSavingVO.getAccType())).thenReturn(optional);
		
		entity.setAccType(optional.get());
		AccountStatus accountStatus = new AccountStatus();
		accountStatus.setId(1);
		entity.setStatus(accountStatus);
		
		CustomerSaving savingEntity = new CustomerSaving();
		BeanUtils.copyProperties(entity,savingEntity, new String[] { "accType", "status" });
		savingEntity.setCsaid(2020);
		savingEntity.setAccType(entity.getAccType());
		savingEntity.setStatus(entity.getStatus());
		when(customerAccountEnquiryRepository.save(entity)).thenReturn(savingEntity);
		
		when(emailService.sendEquiryEmail(new EmailVO(customerSavingVO.getEmail(), "javahunk100@gmail.com", null,
				"Hello! your account enquiry is submitted successfully.", customerSavingVO.getName()))).thenReturn("done");
		
		CustomerSavingVO actual = customerEnquiryServiceImpl.save(customerSavingVO);
		assertNotNull(actual);
		assertEquals(2020, actual.getCsaid());
		assertEquals("saving", savingEntity.getAccType().getName());
		assertEquals(1, savingEntity.getStatus().getId());
		verify(customerAccountEnquiryRepository, times(1)).save(entity);
 	    verifyNoMoreInteractions(customerAccountEnquiryRepository);
 	    verify(emailService, times(1)).sendEquiryEmail(new EmailVO(customerSavingVO.getEmail(), "javahunk100@gmail.com", null,
				"Hello! your account enquiry is submitted successfully.", customerSavingVO.getName()));
 	    verifyNoMoreInteractions(emailService);
 	    verify(accountTypeRepository, times(1)).findByName(customerSavingVO.getAccType());
	    verifyNoMoreInteractions(accountTypeRepository);
		
	}
	
	@Rule
    public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testSaveWhenAccountTypeNotPresent() {
		CustomerSavingVO customerSavingVO = new CustomerSavingVO();
		customerSavingVO.setAccType("abc");
		customerSavingVO.setEmail("r@gmail.com");
		CustomerSaving entity = new CustomerSaving();
		BeanUtils.copyProperties(customerSavingVO, entity, new String[] { "accType", "status" });
		Optional<AccountType> optional = Optional.empty();
		when(accountTypeRepository.findByName(customerSavingVO.getAccType())).thenReturn(optional);
		
		thrown.expect(BankServiceException.class);
		thrown.expectMessage("Hey this " + customerSavingVO.getAccType() + " account type is not valid!");
		customerEnquiryServiceImpl.save(customerSavingVO);	
		
	}

}
