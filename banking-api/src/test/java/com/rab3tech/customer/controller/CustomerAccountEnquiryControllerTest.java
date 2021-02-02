package com.rab3tech.customer.controller;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.service.exception.BankServiceException;
import com.rab3tech.test.TestUtil;
import com.rab3tech.vo.CustomerSavingVO;

public class CustomerAccountEnquiryControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private CustomerEnquiryService customerEnquiryService;
	
	 @InjectMocks
	  private CustomerAccountEnquiryController customerAccountEnquiryController;
	
	@Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerAccountEnquiryController)
              //  .addFilters(new CorsFilter())
                .build();
    }
	

	@Test
	public 	void testGetAllEnquiryWhenEnquiryNoEmpty() throws Exception {
		 List<CustomerSavingVO> customerSavingVOs=new ArrayList<>();
		 CustomerSavingVO customerSavingVO1=new CustomerSavingVO(122,"nagendra","nagen@gmail.com","02390","NA","Saving","Appoved","C9393",null,"A435");
		 CustomerSavingVO customerSavingVO2=new CustomerSavingVO(123,"ashish","ashish@gmail.com","92882","NT","Current","Pending","S9393",null,"B435");
		 customerSavingVOs.add(customerSavingVO1);
		 customerSavingVOs.add(customerSavingVO2);
		 when(customerEnquiryService.findAll()).thenReturn(customerSavingVOs);
		 mockMvc.perform(get("/v3/customers/enquiry"))
         .andExpect(status().isOk())
         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
         .andExpect(jsonPath("$", hasSize(2)))
         .andExpect(jsonPath("$[0].csaid", is(122)))
         .andExpect(jsonPath("$[0].name", is("nagendra")))
         .andExpect(jsonPath("$[0].email", is("nagen@gmail.com")))
         .andExpect(jsonPath("$[1].csaid", is(123)))
         .andExpect(jsonPath("$[1].email", is("ashish@gmail.com")))
         .andExpect(jsonPath("$[1].name", is("ashish")));		 
		 verify(customerEnquiryService, times(1)).findAll();
	     verifyNoMoreInteractions(customerEnquiryService);
	}
	
	@Test
	public 	void testGetAllEnquiryWhenEnquiryIsEmpty() throws Exception {
		 List<CustomerSavingVO> customerSavingVOs=new ArrayList<>();
		 when(customerEnquiryService.findAll()).thenReturn(customerSavingVOs);
		 mockMvc.perform(get("/v3/customers/enquiry"))
         .andExpect(status().isOk())
         .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
         .andExpect(jsonPath("$", hasSize(0)));
		 verify(customerEnquiryService, times(1)).findAll();
	     verifyNoMoreInteractions(customerEnquiryService);
	}
	
	@Test
	public	void testSaveEnquiryWhenNoExcetption() throws Exception {
		  CustomerSavingVO customerSavingVO=new CustomerSavingVO(122,"nagendra","nagen@gmail.com","02390","NA","Saving","Appoved","C9393",null,"A435");
		  when(customerEnquiryService.emailNotExist("nagen@gmail.com")).thenReturn(true);
	 	  when(customerEnquiryService.save(customerSavingVO)).thenReturn(customerSavingVO);
	 	 mockMvc.perform(MockMvcRequestBuilders.post("/v3/customers/enquiry")
	 	        .contentType(MediaType.APPLICATION_JSON)
	 	        .content(TestUtil.convertObjectToJsonBytes(customerSavingVO))
	 			.accept(MediaType.APPLICATION_JSON))
	 			.andExpect(jsonPath("$.name").exists())
	 			.andExpect(jsonPath("$.email").exists())
	 			.andExpect(jsonPath("$.name").value("nagendra"))
	 			.andExpect(jsonPath("$.email").value("nagen@gmail.com"))
	 			.andDo(print());
		 verify(customerEnquiryService, times(1)).save(customerSavingVO);
	     //verifyNoMoreInteractions(customerEnquiryService);
	}
	
	@Test
	@Ignore
	public	void testSaveEnquiryWhenThrowsException() throws Exception {
		  String exceptionParam = "Sorry , this email is already in use nagen@gmail.com";
		  CustomerSavingVO customerSavingVO=new CustomerSavingVO(122,"nagendra","nagen@gmail.com","02390","NA","Saving","Appoved","C9393",null,"A435");
		  when(customerEnquiryService.emailNotExist("nagen@gmail.com")).thenReturn(false);
		  mockMvc.perform(MockMvcRequestBuilders.post("/v3/customers/enquiry",exceptionParam)
				  .contentType(MediaType.APPLICATION_JSON)
		 	        .content(TestUtil.convertObjectToJsonBytes(customerSavingVO))
	 	        .contentType(MediaType.APPLICATION_JSON))
			 	.andExpect(status().isBadRequest())
			 	.andExpect(result -> assertTrue(result.getResolvedException() instanceof BankServiceException))
			    .andExpect(result -> assertEquals(exceptionParam, result.getResolvedException().getMessage()))
		        .andReturn();
	}
	
	@Test
	public	void testGetEnquiryByIdWhenExist() throws Exception {
		 CustomerSavingVO customerSavingVO=new CustomerSavingVO(122,"nagendra","nagen@gmail.com","02390","NA","Saving","Appoved","C9393",null,"A435");
		 when(customerEnquiryService.findById(122)).thenReturn(customerSavingVO);
	 	 mockMvc.perform(MockMvcRequestBuilders.get("/v3/customers/enquiry/"+122)
     	 			 .accept(MediaType.APPLICATION_JSON))
		 			.andExpect(jsonPath("$.name").exists())
		 			.andExpect(jsonPath("$.email").exists())
		 			.andExpect(jsonPath("$.name").value("nagendra"))
		 			.andExpect(jsonPath("$.email").value("nagen@gmail.com"))
		 			.andDo(print());
		 verify(customerEnquiryService, times(1)).findById(122);
	     verifyNoMoreInteractions(customerEnquiryService);
	}
	
	@Test
	public void testgetAllPendingEnquiry() throws Exception{
		List<CustomerSavingVO> customerSavingVOs=new ArrayList<>();
		CustomerSavingVO customerSavingVO1=new CustomerSavingVO(122,"nagendra","nagen@gmail.com","02390","NA","Saving","Pending","C9393",null,"A435");
		CustomerSavingVO customerSavingVO2=new CustomerSavingVO(123,"ashish","ashish@gmail.com","92882","NT","Current","Pending","S9393",null,"B435");
		customerSavingVOs.add(customerSavingVO1);
		customerSavingVOs.add(customerSavingVO2);
		when(customerEnquiryService.findPendingEnquiry()).thenReturn(customerSavingVOs);
		
		mockMvc.perform(get("/v3/customers/enquiry/pending"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].csaid", is(122)))
        .andExpect(jsonPath("$[0].status", is("Pending")))
        .andExpect(jsonPath("$[0].email", is("nagen@gmail.com")))
        .andExpect(jsonPath("$[1].csaid", is(123)))
        .andExpect(jsonPath("$[1].email", is("ashish@gmail.com")))
        .andExpect(jsonPath("$[1].status", is("Pending")));		 
		 verify(customerEnquiryService, times(1)).findPendingEnquiry();
	     verifyNoMoreInteractions(customerEnquiryService);
		
		
	}
	
}
