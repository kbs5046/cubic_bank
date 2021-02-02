package com.rab3tech.customer.angular.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rab3tech.customer.service.LoginService;
import com.rab3tech.customer.service.impl.CustomerEnquiryService;
import com.rab3tech.vo.ApplicationResponseVO;
import com.rab3tech.vo.CustomerSavingVO;
import com.rab3tech.vo.LoginRequestVO;
import com.rab3tech.vo.LoginVO;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/v4")
public class CustomerAngularController {
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CustomerEnquiryService customerEnquiryService;
	
	@PostMapping("/user/login")
 	public ApplicationResponseVO authUser(@RequestBody LoginRequestVO loginRequestVO) {
		
 		Optional<LoginVO>  optional=loginService.findUserByUsername(loginRequestVO.getUsername());
 		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
 		if(optional.isPresent()) {
 			boolean  optional2 = loginService.checkPasswordValid(loginRequestVO.getUsername(), loginRequestVO.getPassword());
 			if(optional2) {
	 			applicationResponseVO.setCode(200);
	 			applicationResponseVO.setStatus("success");
	 			applicationResponseVO.setMessage(optional.get().getName());
 			}
 			else {
 				applicationResponseVO.setCode(400);
 	 			applicationResponseVO.setStatus("fail");
 	 			applicationResponseVO.setMessage("Please enter correct password..");
 			}
 		}else {
 			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage("Username or password is not correct");
 		}
 		return applicationResponseVO;
 	}
	
	@PostMapping("/submit/enquiry")
	public ApplicationResponseVO submitEnquiryData(@RequestBody CustomerSavingVO customerSavingVO) {
		ApplicationResponseVO applicationResponseVO=new ApplicationResponseVO();
		boolean status = customerEnquiryService.emailNotExist(customerSavingVO.getEmail());
		if(status) {
			CustomerSavingVO response = customerEnquiryService.save(customerSavingVO);
			
			applicationResponseVO.setCode(200);
 			applicationResponseVO.setStatus("success");
 			applicationResponseVO.setMessage("Hey Customer , your enquiry form has been submitted successfully!!! and appref "
					+ response.getAppref());
		}
		else {
			applicationResponseVO.setCode(400);
 			applicationResponseVO.setStatus("fail");
 			applicationResponseVO.setMessage("Sorry , this email is already in use " + customerSavingVO.getEmail());
		}
		return applicationResponseVO;
	}
	

}
