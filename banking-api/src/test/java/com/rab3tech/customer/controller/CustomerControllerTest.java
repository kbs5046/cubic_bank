package com.rab3tech.customer.controller;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.rab3tech.customer.service.CustomerRequestService;
import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.PayeeService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.customer.service.impl.SecurityQuestionService;

public class CustomerControllerTest {
	
	@Mock
	private MockMvc mockMvc;
	
	@Mock
	private LoginService loginService;
	
	@Mock
	private CustomerEnquiryService customerEnquiryService;
	
	@Mock
	private SecurityQuestionService securityQuestionService;
	
	@Mock
	private PayeeService payeeService;
	
	@Mock
	private CustomerRequestService customerRequestService;
	
	@InjectMocks
	private CustomerController customerController;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
              //  .addFilters(new CorsFilter())
                .build();
	}

}
